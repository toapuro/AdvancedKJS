package dev.toapuro.advancedkjs.api.config;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import dev.toapuro.advancedkjs.content.js.bundle.pack.AdvancedKubeJSPaths;

import java.nio.file.Path;

public class SWCConfigFileGenerator extends ConfigFileGenerator {
    public static Path CONFIG_PATH = AdvancedKubeJSPaths.SRC.resolve(".swcrc");
    public static SWCConfigFileGenerator GENERATOR = new SWCConfigFileGenerator(CONFIG_PATH);

    public SWCConfigFileGenerator(Path path) {
        super(path);
    }

    @Override
    public JsonObject buildDefaultConfig() {
        return inObject(root -> {
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
        });
    }
}
