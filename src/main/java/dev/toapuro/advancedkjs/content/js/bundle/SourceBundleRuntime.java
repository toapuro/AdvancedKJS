package dev.toapuro.advancedkjs.content.js.bundle;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.toapuro.advancedkjs.content.js.bundle.esbuild.ESBuildRuntimeWrapper;
import dev.toapuro.advancedkjs.content.js.bundle.esbuild.ESBuildWrapper;
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
    private final Path sourcePath;
    private final Path buildPath;
    private final ESBuildRuntimeWrapper wrapper;

    public SourceBundleRuntime(ESBuildWrapper wrapper, ScriptType scriptType, Path sourcePath, Path buildPath, List<String> entryPoints) {
        this.scriptType = scriptType;
        this.sourcePath = sourcePath;
        this.buildPath = buildPath;
        this.wrapper = new ESBuildRuntimeWrapper(wrapper, sourcePath, entryPoints, Map.of());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteBuildFiles() {
        File buildPathFile = buildPath.toFile();

        if (buildPathFile.isDirectory()) {
            File[] files = buildPathFile.listFiles();
            if (files == null) return;
            for (File file : files) {
                if (file.isFile()) file.delete();
            }
        }
    }

    public List<SourceBundle> resolveBuild() {
        File buildPathFile = buildPath.resolve("bundle").toFile();

        this.deleteBuildFiles();

        String output;
        try {
            output = wrapper.run(sourcePath);
        } catch (IOException e) {
            LOGGER.error("Error occurred bundling sources", e);
            throw new RuntimeException(e);
        }

        LOGGER.info("Files bundled: {}", output);

        List<SourceBundle> sourceBundles = new ArrayList<>();

        for (File file : Optional.ofNullable(buildPathFile.listFiles((dir, name) -> name.endsWith(".js"))).orElse(new File[0])) {
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

            SourceBundle bundle = new SourceBundle(this, file.getName(), lines);
            sourceBundles.add(bundle);
        }

        return sourceBundles;
    }

    public ScriptType getScriptType() {
        return scriptType;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public ESBuildRuntimeWrapper getWrapper() {
        return wrapper;
    }
}
