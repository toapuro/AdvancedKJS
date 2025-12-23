package dev.toapuro.advancedkjs.content.js.typejs;

import com.google.gson.JsonObject;
import dev.toapuro.advancedkjs.api.config.ConfigFileGenerator;

import java.nio.file.Path;

public class TypeJSConfigGenerator extends ConfigFileGenerator {
    public TypeJSConfigGenerator(Path filePath) {
        super(filePath);
    }

    @Override
    public JsonObject buildDefaultConfig() {
        return inObject(root -> {
            root.add("compilerOptions", inObject(options -> {
                options.addProperty("experimentalDecorators", true);
                options.addProperty("useDefineForClassFields", false);
            }));
        });
    }
}
