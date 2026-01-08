package dev.toapuro.advancedkjs.content.js.bundle.pack;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.script.ScriptType;

import java.nio.file.Path;

public interface AdvancedKubeJSPaths extends KubeJSPaths {
    Path SRC = DIRECTORY.resolve("src/");

    // src/
    Path STARTUP_SCRIPTS = SRC.resolve("startup_scripts");
    Path SERVER_SCRIPTS = SRC.resolve("server_scripts");
    Path CLIENT_SCRIPTS = SRC.resolve("client_scripts");

    static Path fromScriptType(ScriptType scriptType) {
        return switch (scriptType) {
            case STARTUP -> AdvancedKubeJSPaths.STARTUP_SCRIPTS;
            case SERVER -> AdvancedKubeJSPaths.SERVER_SCRIPTS;
            case CLIENT -> AdvancedKubeJSPaths.CLIENT_SCRIPTS;
        };
    }
}
