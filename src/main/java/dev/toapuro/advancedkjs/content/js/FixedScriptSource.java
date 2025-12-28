package dev.toapuro.advancedkjs.content.js;

import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptSource;

import java.io.IOException;
import java.util.List;

public record FixedScriptSource(List<String> lines) implements ScriptSource {

    @Override
    public List<String> readSource(ScriptFileInfo scriptFileInfo) throws IOException {
        return lines;
    }
}
