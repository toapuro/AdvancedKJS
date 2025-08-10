package dev.toapuro.advancedkjs.content.js.bundle;

import dev.latvian.mods.kubejs.script.ScriptFile;
import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.toapuro.advancedkjs.content.js.bundle.esbuild.ESBuildWrapper;
import dev.toapuro.advancedkjs.content.js.bundle.pack.BundleScriptPack;
import dev.toapuro.advancedkjs.content.js.bundle.pack.BundleScriptPackInfo;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SourceBundleHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceBundleHandler.class);
    private static final String ENTRY_POINT = "index.ts";

    private final ESBuildWrapper esBuild;
    private final SourceBundleRuntime runtime;

    public SourceBundleHandler(ScriptType scriptType, Path sourcePath) {
        this.esBuild = new ESBuildWrapper();
        this.runtime = new SourceBundleRuntime(esBuild, scriptType, sourcePath, sourcePath.resolve("build"), List.of(ENTRY_POINT));
    }

    public void init() {
        esBuild.init();
    }

    public boolean isValidFile(ResourceLocation resourceLocation) {
        return resourceLocation.getPath().endsWith(".ts") ||
                resourceLocation.getPath().endsWith(".js");
    }

    public BundleScriptPack bundleScripts(ScriptManager scriptManager) {
        return loadBundleFromFile(scriptManager, runtime);
    }

    public BundleScriptPack loadBundleFromFile(ScriptManager scriptManager, SourceBundleRuntime bundleRuntime) {
        ScriptType scriptType = bundleRuntime.getScriptType();
        Path sourcePath = bundleRuntime.getSourcePath();

        if (Files.notExists(sourcePath)) {
            try {
                Files.createDirectories(sourcePath);
            } catch (Exception ex) {
                LOGGER.error("Failed to create script directory", ex);
                return null;
            }
        }

        if (Files.notExists(sourcePath.resolve(ENTRY_POINT))) {
            try (OutputStream out = Files.newOutputStream(sourcePath.resolve(ENTRY_POINT))) {
                String content = String.format("// %s entry point", scriptType);
                out.write(content.getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                LOGGER.error("Failed to write bundle file {}", scriptType, ex);
                return null;
            }
        }

        BundleScriptPack pack = null;
        try {
            pack = loadBundleSource(scriptManager, bundleRuntime);
        } catch (Throwable e) {
            LOGGER.error("Failed to load bundle file {}", scriptType, e);
            throw new RuntimeException(e);
        }

        return pack;
    }

    public BundleScriptPack loadBundleSource(ScriptManager scriptManager, SourceBundleRuntime bundleRuntime) throws Throwable {
        BundleScriptPackInfo packInfo = new BundleScriptPackInfo("build.bundle." + bundleRuntime.getScriptType().name, "");
        BundleScriptPack pack = new BundleScriptPack(scriptManager, packInfo);

        List<SourceBundle> bundles = bundleRuntime.resolveBuild();

        for (SourceBundle bundle : bundles) {
            ScriptFileInfo fileInfo = new ScriptFileInfo(packInfo, bundle.name());
            BundleScriptSource source = new BundleScriptSource(bundle);
            fileInfo.preload(source);

            pack.scripts.add(new ScriptFile(pack, fileInfo, source));
        }

        return pack;
    }
}
