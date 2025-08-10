package dev.toapuro.advancedkjs.mixin.core;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.latvian.mods.kubejs.script.*;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.advancedkjs.content.claasgen.handler.AdvancedKJSClassLoader;
import dev.toapuro.advancedkjs.content.claasgen.handler.ClassLoaderHandler;
import dev.toapuro.advancedkjs.content.js.bundle.SourceBundleHandler;
import dev.toapuro.advancedkjs.content.js.bundle.pack.AdvancedKubeJSPaths;
import dev.toapuro.advancedkjs.content.js.swc.SWCHandler;
import dev.toapuro.advancedkjs.mixin.helper.IMixin;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.Map;

@SuppressWarnings("CommentedOutCode")
@Mixin(value = ScriptManager.class, remap = false)
@Debug(export = true)
public abstract class ScriptManagerMixin implements IMixin<ScriptManager> {
    @Unique
    public final SWCHandler akjs$swcHandler = new SWCHandler("compile");
    @Shadow
    public Scriptable topLevelScope;
    @Shadow
    public Context context;
    @Shadow
    @Final
    public ScriptType scriptType;
    @Shadow
    @Final
    public Map<String, ScriptPack> packs;
    @Unique
    public SourceBundleHandler akjs$bundleHandler;
    @Unique
    public Path akjs$scriptSourcePath;

    @ModifyExpressionValue(method = "lambda$loadFromResources$0", at = {
            @At(value = "INVOKE", target = "Ljava/lang/String;endsWith(Ljava/lang/String;)Z", ordinal = 1)
    })
    private static boolean disableTsFile(boolean original) {
        return false;
    }

    @Inject(method = "loadFromResources", at = @At(value = "HEAD"))
    public void loadFromResources(ResourceManager resourceManager, CallbackInfo ci) {
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void init(ScriptType t, CallbackInfo ci) {
        this.akjs$scriptSourcePath = AdvancedKubeJSPaths.fromScriptType(scriptType);
        this.akjs$bundleHandler = new SourceBundleHandler(scriptType, akjs$scriptSourcePath);

        akjs$bundleHandler.init();
    }

    @Inject(method = "reload", at = @At(value = "INVOKE", target = "Ldev/latvian/mods/kubejs/script/ScriptManager;loadFromDirectory()V"))
    public void beforeLoad(ResourceManager resourceManager, CallbackInfo ci) {
        String namespace = "build." + scriptType.name;

        ConsoleJS console = scriptType.console;
        console.info("Building sources");
        ScriptPack loadedPack = akjs$bundleHandler.bundleScripts(castSelf());
        ScriptPack compiledPack = akjs$swcHandler.compileScripts(castSelf(), loadedPack, akjs$scriptSourcePath);

        packs.put(namespace, compiledPack);
    }

    @Inject(method = "loadFile", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), cancellable = true)
    public void loadFile(ScriptPack pack, ScriptFileInfo fileInfo, ScriptSource source, CallbackInfo ci) {
        if (fileInfo.file.endsWith(".ts")) {
            ci.cancel();
        }
    }

    @Inject(method = "loadJavaClass", at = @At("HEAD"), cancellable = true)
    public void loadJavaClass(String name, boolean error, CallbackInfoReturnable<NativeJavaClass> cir) {
        AdvancedKJSClassLoader currentClassLoader = ClassLoaderHandler.getCurrentClassLoader();
        if (currentClassLoader == null) return;
        Map<String, Class<?>> classMap = currentClassLoader.getGenClassLookup();
        if (classMap.containsKey(name)) {
            cir.setReturnValue(
                    new NativeJavaClass(this.context, this.topLevelScope, classMap.get(name))
            );
        }
    }
}
