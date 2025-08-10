package dev.toapuro.advancedkjs.content.js.bundle.pack;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.script.ScriptType;

import java.nio.file.Path;

public interface AdvancedKubeJSPaths extends KubeJSPaths {
    Path SRC = DIRECTORY.resolve("src/");
    Path STARTUP_SCRIPTS = DIRECTORY.resolve("src/startup_scripts");
    Path SERVER_SCRIPTS = DIRECTORY.resolve("src/server_scripts");
    Path CLIENT_SCRIPTS = DIRECTORY.resolve("src/client_scripts");

    static Path fromScriptType(ScriptType scriptType) {
        return switch (scriptType) {
            case STARTUP -> AdvancedKubeJSPaths.STARTUP_SCRIPTS;
            case SERVER -> AdvancedKubeJSPaths.SERVER_SCRIPTS;
            case CLIENT -> AdvancedKubeJSPaths.CLIENT_SCRIPTS;
        };
    }
}
