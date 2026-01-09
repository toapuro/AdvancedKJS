package dev.toapuro.advancedkjs.content.typejs.bundle;

import dev.latvian.mods.kubejs.script.ScriptFile;
import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.toapuro.advancedkjs.content.typejs.bundle.esbuild.ESBuildWrapper;
import dev.toapuro.advancedkjs.content.typejs.bundle.pack.BundleScriptPack;
import dev.toapuro.advancedkjs.content.typejs.bundle.pack.BundleScriptPackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SourceBundleHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceBundleHandler.class);
    private static final String ENTRYPOINT_FILE = "index.ts";

    private final ESBuildWrapper esBuild;
    private final SourceBundleRuntime runtime;

    private final Path sourcePath;

    public SourceBundleHandler(ScriptType scriptType, Path rootPath, Path sourcePath) {
        this.esBuild = new ESBuildWrapper();
        this.runtime = new SourceBundleRuntime(
                esBuild, scriptType, rootPath, sourcePath.resolve("build"),
                List.of(sourcePath.resolve(ENTRYPOINT_FILE).toString())
        );
        this.sourcePath = sourcePath;
    }

    public void init() {
        esBuild.init();
    }

    public BundleScriptPack bundleScripts(ScriptManager scriptManager) {
        ScriptType scriptType = runtime.getScriptType();

        try {
            return buildBundles(scriptManager);
        } catch (Throwable e) {
            LOGGER.error("Failed to load bundle file {}", scriptType, e);
            throw new RuntimeException(e);
        }
    }

    public BundleScriptPack buildBundles(ScriptManager scriptManager) throws Throwable {
        BundleScriptPackInfo packInfo = new BundleScriptPackInfo("build.bundle." + runtime.getScriptType().name, "");
        BundleScriptPack pack = new BundleScriptPack(scriptManager, packInfo);

        List<BundleSource> bundles = runtime.buildBundles();

        for (BundleSource bundle : bundles) {
            ScriptFileInfo fileInfo = new ScriptFileInfo(packInfo, bundle.name());

            BundleScriptSource source = new BundleScriptSource(bundle);
            fileInfo.preload(source);

            pack.scripts.add(new ScriptFile(pack, fileInfo, source));
        }

        return pack;
    }

    public void createExampleFiles() {
        ScriptType scriptType = runtime.getScriptType();

        if (Files.notExists(sourcePath)) {
            try {
                Files.createDirectories(sourcePath);
            } catch (Exception ex) {
                LOGGER.error("Failed to create script directory", ex);
            }
        }

        Path entryPath = sourcePath.resolve(ENTRYPOINT_FILE);
        if (Files.notExists(entryPath)) {
            try (OutputStream out = Files.newOutputStream(entryPath)) {
                String content = String.format("// %s entry point", scriptType);
                out.write(content.getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                LOGGER.error("Failed to write bundle file {}", scriptType, ex);
            }
        }
    }
}
