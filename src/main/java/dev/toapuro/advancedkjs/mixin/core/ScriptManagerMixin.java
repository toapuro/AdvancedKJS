package dev.toapuro.advancedkjs.mixin.core;

import dev.latvian.mods.kubejs.script.*;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.toapuro.advancedkjs.api.config.SWCConfigFileGenerator;
import dev.toapuro.advancedkjs.content.js.FixedScriptSource;
import dev.toapuro.advancedkjs.content.js.bundle.SourceBundleHandler;
import dev.toapuro.advancedkjs.content.js.bundle.pack.AdvancedKubeJSPaths;
import dev.toapuro.advancedkjs.content.js.swc.SWCCommandHandler;
import dev.toapuro.advancedkjs.content.js.typejs.TypeJSConfigGenerator;
import dev.toapuro.advancedkjs.mixin.helper.MixinUtil;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@SuppressWarnings("CommentedOutCode")
@Mixin(value = ScriptManager.class, remap = false)
@Debug(export = true)
public abstract class ScriptManagerMixin {

    @Shadow
    @Final
    public ScriptType scriptType;
    @Shadow
    @Final
    public Map<String, ScriptPack> packs;

    @Unique
    public final SWCCommandHandler akjs$swcCommandHandler = new SWCCommandHandler("compile");
    @Unique
    public SourceBundleHandler akjs$bundleHandler;
    @Unique
    public Path akjs$scriptSourcePath;

    @Inject(method = "loadFile", at = @At(value = "HEAD"), cancellable = true)
    public void loadFile(ScriptPack pack, ScriptFileInfo fileInfo, ScriptSource rawSource, CallbackInfo ci) throws IOException {
        if (!fileInfo.file.endsWith(".ts")) {
            return;
        }
        List<String> rawLines = rawSource.readSource(fileInfo);

        // compile
        List<String> outputLines = akjs$swcCommandHandler.compileScript(rawLines, akjs$scriptSourcePath);

        ScriptSource source = new FixedScriptSource(outputLines);

        try {
            fileInfo.preload(source);
            String skip = fileInfo.skipLoading();
            if (skip.isEmpty()) {
                pack.scripts.add(new ScriptFile(pack, fileInfo, source));
            } else {
                this.scriptType.console.info("Skipped " + fileInfo.location + ": " + skip);
            }
        } catch (Throwable error) {
            this.scriptType.console.error("Failed to pre-load script file '" + fileInfo.location + "'", error);
        }

        ci.cancel();
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void init(ScriptType t, CallbackInfo ci) {
        this.akjs$scriptSourcePath = AdvancedKubeJSPaths.fromScriptType(scriptType);
        this.akjs$bundleHandler = new SourceBundleHandler(scriptType, AdvancedKubeJSPaths.SRC, akjs$scriptSourcePath);

        akjs$bundleHandler.init();

        TypeJSConfigGenerator.DEFAULT.createIfNotExists();
        SWCConfigFileGenerator.DEFAULT.createIfNotExists();
        akjs$bundleHandler.createExampleFiles();
    }

    @Inject(method = "reload", at = @At(value = "INVOKE", target = "Ldev/latvian/mods/kubejs/script/ScriptManager;loadFromDirectory()V"))
    public void beforeLoad(ResourceManager resourceManager, CallbackInfo ci) {
        ScriptManager scriptManager = MixinUtil.cast(this);


        String namespace = "build." + scriptType.name;

        ConsoleJS console = scriptType.console;
        console.info("Bundling sources");
        ScriptPack bundledPack = akjs$bundleHandler.bundleScripts(scriptManager);

        console.info("Compiling sources to typescript");
        ScriptPack compiledPack = akjs$swcCommandHandler.compileScripts(scriptManager, bundledPack, akjs$scriptSourcePath);
        if (compiledPack == null) {
            console.info("Failed to build sources");
            return;
        }

        packs.put(namespace, compiledPack);
    }
}
