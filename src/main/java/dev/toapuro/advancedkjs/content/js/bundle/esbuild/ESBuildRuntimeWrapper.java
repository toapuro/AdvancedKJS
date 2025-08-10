package dev.toapuro.advancedkjs.content.js.bundle.esbuild;

import io.mvnpm.esbuild.model.EsBuildConfig;
import io.mvnpm.esbuild.model.EsBuildConfigBuilder;
import io.mvnpm.esbuild.model.ExecuteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ESBuildRuntimeWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESBuildRuntimeWrapper.class);

    private final ESBuildWrapper wrapper;
    private final Path sourcePath;
    private final List<String> entryPoints;
    private final EsBuildConfig config;

    private final Map<String, String> valueDefinitions;

    public ESBuildRuntimeWrapper(ESBuildWrapper wrapper, Path sourcePath, List<String> entryPoints, Map<String, String> valueDefinitions) {
        this.wrapper = wrapper;
        this.sourcePath = sourcePath;
        this.entryPoints = entryPoints;
        this.valueDefinitions = valueDefinitions;
        this.config = buildConfig();
    }

    public EsBuildConfig buildConfig() {
        EsBuildConfigBuilder baseConfig = EsBuildConfig.builder()
                .bundle()
                .entryPoint(this.entryPoints.toArray(String[]::new))
                .entryNames("[dir]/[name]-bundle")
                .outDir("build/bundle");

        baseConfig.define(valueDefinitions);

        return baseConfig.build();
    }

    public String run(Path workingDir) throws IOException {
        ExecuteResult execute = wrapper.execute(workingDir, config);
        return execute.output();
    }
}
