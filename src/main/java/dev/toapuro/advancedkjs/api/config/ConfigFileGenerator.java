package dev.toapuro.advancedkjs.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public abstract class ConfigFileGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFileGenerator.class);
    private final Path filePath;

    public ConfigFileGenerator(Path filePath) {
        this.filePath = filePath;

    }

    protected static JsonObject object(UnaryOperator<JsonObject> configOperator) {
        return configOperator.apply(new JsonObject());
    }

    protected static JsonObject object(Consumer<JsonObject> consumer) {
        JsonObject jsonObject = new JsonObject();
        consumer.accept(jsonObject);
        return jsonObject;
    }

    protected static JsonArray array(UnaryOperator<JsonArray> configOperator) {
        return configOperator.apply(new JsonArray());
    }

    protected static JsonArray array(Consumer<JsonArray> consumer) {
        JsonArray array = new JsonArray();
        consumer.accept(array);
        return array;
    }

    public void createIfNotExists() {
        if (Files.notExists(this.filePath)) {
            createDefaultConfig();
        }
    }

    private void createDefaultConfig() {
        try (OutputStream out = Files.newOutputStream(filePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String content = gson.toJson(this.buildDefaultConfig());

            out.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            LOGGER.error("Failed to write {} file: ", filePath.getFileName(), ex);
        }
    }

    public Path getFilePath() {
        return filePath;
    }

    public abstract JsonObject buildDefaultConfig();
}
