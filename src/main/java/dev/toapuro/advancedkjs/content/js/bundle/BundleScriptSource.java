package dev.toapuro.advancedkjs.content.js.bundle;

import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptSource;

import java.io.IOException;
import java.util.List;

public record BundleScriptSource(BundleSource bundleSource) implements ScriptSource {

    @Override
    public List<String> readSource(ScriptFileInfo scriptFileInfo) throws IOException {
        return bundleSource.lines();
    }
}
