package dev.toapuro.advancedkjs.content.js.bundle.esbuild;

import com.google.errorprone.annotations.concurrent.LazyInit;
import io.mvnpm.esbuild.Execute;
import io.mvnpm.esbuild.model.EsBuildConfig;
import io.mvnpm.esbuild.model.ExecuteResult;
import io.mvnpm.esbuild.resolve.Resolver;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ESBuildWrapper {
    private final List<String> args;

    @LazyInit
    private Path esbuildPath;

    public ESBuildWrapper() {
        this.args = new ArrayList<>();
    }

    public void addArg(String arg) {
        this.args.add(arg);
    }

    public void init() {
        try {
            esbuildPath = Resolver.create().resolve("0.25.8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ExecuteResult execute(Path workingDirectory, EsBuildConfig esBuildConfig) throws IOException {
        if (esbuildPath == null) {
            throw new IOException("ESbuild is not initialized");
        }

        List<String> params = new ArrayList<>(Arrays.asList(esBuildConfig.toParams()));
        params.addAll(args);

        return new Execute(workingDirectory, esbuildPath.toFile(), params.toArray(String[]::new)).executeAndWait();
    }
}
