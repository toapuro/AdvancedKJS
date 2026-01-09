package dev.toapuro.advancedkjs.content.typejs.bundle;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.toapuro.advancedkjs.content.typejs.bundle.esbuild.ESBuildRuntimeWrapper;
import dev.toapuro.advancedkjs.content.typejs.bundle.esbuild.ESBuildWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SourceBundleRuntime {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceBundleRuntime.class);
    private final ScriptType scriptType;
    private final Path rootPath;
    private final Path outputPath;
    private final ESBuildRuntimeWrapper wrapper;

    public SourceBundleRuntime(ESBuildWrapper wrapper, ScriptType scriptType, Path rootPath, Path outputPath, List<String> entryPoints) {
        this.scriptType = scriptType;
        this.rootPath = rootPath;
        this.outputPath = outputPath;
        this.wrapper = new ESBuildRuntimeWrapper(wrapper, entryPoints, Map.of());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteBuildFiles() {
        File buildPathFile = outputPath.toFile();

        if (buildPathFile.isDirectory()) {
            File[] files = buildPathFile.listFiles();
            if (files == null) return;
            for (File file : files) {
                if (file.isFile()) file.delete();
            }
        }
    }

    public List<BundleSource> buildBundles() {
        File buildPathFile = outputPath.resolve("bundle").toFile();

        this.deleteBuildFiles();

        try {
            wrapper.run(rootPath);
        } catch (IOException e) {
            LOGGER.error("Error occurred bundling sources", e);
            throw new RuntimeException(e);
        }

        File[] files = Optional.ofNullable(buildPathFile.listFiles((dir, name) -> name.endsWith(".js"))).orElse(new File[0]);

        LOGGER.info("Files bundled: {}", files.length);

        List<BundleSource> bundleSources = new ArrayList<>();
        for (File file : files) {
            List<String> lines = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                LOGGER.error("Error occurred reading output", e);
                throw new RuntimeException(e);
            }

            BundleSource bundle = new BundleSource(this, file.getName(), lines);
            bundleSources.add(bundle);
        }

        return bundleSources;
    }

    public ScriptType getScriptType() {
        return scriptType;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public ESBuildRuntimeWrapper getWrapper() {
        return wrapper;
    }
}
