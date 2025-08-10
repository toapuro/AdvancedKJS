package dev.toapuro.advancedkjs.content.js.swc;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import dev.latvian.mods.kubejs.script.*;
import dev.toapuro.advancedkjs.content.js.LoadedScriptSource;
import io.mvnpm.esbuild.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class SWCHandler {
    private static final Path swcExecPath = SWCLibLoader.downloadSwcFile();
    private static final Logger LOGGER = LoggerFactory.getLogger(SWCHandler.class);

    private final String subcommand;

    public SWCHandler(String subcommand) {
        this.subcommand = subcommand;
    }

    private static String readStream(InputStream stream) {
        final StringBuilder s = new StringBuilder();
        consumeStream(() -> true, stream, l -> s.append(l).append("\n"));
        return s.toString();
    }

    private static void consumeStream(BooleanSupplier stayAlive, InputStream stream, Consumer<String> newLineConsumer) {
        try (
                final InputStreamReader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
                final BufferedReader reader = new BufferedReader(in)) {
            String line;
            while ((line = reader.readLine()) != null) {
                newLineConsumer.accept(line);
                if (!stayAlive.getAsBoolean()) {
                    break;
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private Process createProcess(List<String> command, Path workDir) throws IOException {
        return new ProcessBuilder().redirectErrorStream(false).directory(workDir.toFile())
                .command(command).start();
    }

    public List<String> buildCommands(List<String> arguments) {
        List<String> commands = new ArrayList<>();
        commands.add(swcExecPath.toString());
        commands.add(this.subcommand);
        commands.addAll(arguments);
        return commands;
    }

    @SuppressWarnings("UnusedReturnValue")
    public List<String> run(Path workDir, String source, List<String> args) {
        try {
            Process process = createProcess(buildCommands(args), workDir);

            OutputStream outputStream = process.getOutputStream();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                writer.write(source);
                writer.flush();

                outputStream.close();
            }

            int exitCode = process.waitFor();
            String output = readStream(process.getInputStream());
            String errors = readStream(process.getErrorStream());
            if (exitCode != 0) {
                throw new BundleException(errors.isEmpty() ? "Unexpected Error during bundling" : errors, output);
            }

            return new ArrayList<>(Splitter.on("\n").splitToList(output));
        } catch (InterruptedException | IOException | BundleException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public ScriptPack compileScripts(ScriptManager scriptManager, ScriptPack loadedPack, Path sourcePath) {
        ScriptType scriptType = scriptManager.scriptType;
        String namespace = "build." + scriptType.name;

        try {
            for (ScriptFile script : loadedPack.scripts) {
                List<String> inputLines = script.source.readSource(script.info);
                String inputSource = Joiner.on("\n").join(inputLines);

                String fileName = "index" + ".js";
                this.run(sourcePath, inputSource, List.of(
                        "--out-dir", "build",
                        "-f", fileName
                ));

                return this.loadOutputFile(namespace, scriptManager, sourcePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return loadedPack;
    }

    public ScriptPack loadOutputFile(String namespace, ScriptManager scriptManager, Path sourcePath) {
        File buildPathFile = sourcePath.resolve("build").toFile();

        ScriptPack outputPack = new ScriptPack(scriptManager, new ScriptPackInfo(namespace, ""));

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

            ScriptFileInfo fileInfo = new ScriptFileInfo(outputPack.info, file.getName());
            outputPack.scripts.add(new ScriptFile(outputPack, fileInfo, new LoadedScriptSource(lines)));
        }

        return outputPack;
    }
}
