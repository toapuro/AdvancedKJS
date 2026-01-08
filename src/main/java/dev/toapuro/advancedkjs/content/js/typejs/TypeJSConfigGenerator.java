package dev.toapuro.advancedkjs.content.js.typejs;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.toapuro.advancedkjs.api.config.ConfigFileGenerator;
import dev.toapuro.advancedkjs.api.config.SWCConfigFileGenerator;

import java.nio.file.Path;

public class TypeJSConfigGenerator extends ConfigFileGenerator {
    public static SWCConfigFileGenerator DEFAULT = new SWCConfigFileGenerator(
            KubeJSPaths.DIRECTORY.resolve("tsconfig")
    );

    public TypeJSConfigGenerator(Path filePath) {
        super(filePath);
    }

    @Override
    public JsonObject buildDefaultConfig() {
        return object(root -> {
            root.add("compilerOptions", object(options -> {
                options.addProperty("experimentalDecorators", true);
                options.addProperty("useDefineForClassFields", false);

                // TODO: 確認
                options.addProperty("noCheck", true);

                options.add("rootDirs", array(rootDirs -> {
                    // ProbeJS Support
                    rootDirs.add("probe/generated");
                    rootDirs.add("probe/user");
                    rootDirs.add("server_scripts");
                    rootDirs.add("startup_scripts");
                    rootDirs.add("client_scripts");
                    rootDirs.add("src");
                }));
            }));
        });
    }
}
