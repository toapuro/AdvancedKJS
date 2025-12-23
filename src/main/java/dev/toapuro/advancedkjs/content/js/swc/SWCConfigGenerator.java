package dev.toapuro.advancedkjs.content.js.swc;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import dev.toapuro.advancedkjs.api.config.ConfigFileGenerator;
import dev.toapuro.advancedkjs.content.js.bundle.pack.AdvancedKubeJSPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class SWCConfigGenerator extends ConfigFileGenerator {
    public static final Path swcrcConfigPath = AdvancedKubeJSPaths.SRC.resolve(".swcrc");
    private static final Logger LOGGER = LoggerFactory.getLogger(SWCConfigGenerator.class);

    public SWCConfigGenerator(Path filePath) {
        super(filePath);
    }

    @Override
    public JsonObject buildDefaultConfig() {
        JsonObject root = new JsonObject();

        root.addProperty("$schema", "https://swc.rs/schema.json");

        root.add("jsc", inObject(jsc -> {
            jsc.add("parser", inObject(parser -> {
                parser.addProperty("syntax", "typescript");
                parser.addProperty("jsx", false);
                parser.addProperty("dynamicImport", false);
                parser.addProperty("privateMethod", false);
                parser.addProperty("functionBind", false);
                parser.addProperty("exportDefaultFrom", false);
                parser.addProperty("exportNamespaceFrom", false);
                parser.addProperty("decorators", true);
                parser.addProperty("decoratorsBeforeExport", false);
                parser.addProperty("topLevelAwait", false);
                parser.addProperty("importMeta", false);
            }));

            jsc.add("transform", JsonNull.INSTANCE);
            jsc.addProperty("target", "es3");
            jsc.addProperty("loose", true);
            jsc.addProperty("externalHelpers", false);
            jsc.addProperty("keepClassNames", true);
        }));

        root.addProperty("minify", false);

        return root;
    }
}
