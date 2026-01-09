package dev.toapuro.advancedkjs.content.typejs.bundle.pack;

import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptPackInfo;

import java.util.ArrayList;
import java.util.List;

public class BundleScriptPackInfo extends ScriptPackInfo {
    public final List<ScriptFileInfo> bundleScripts;

    public BundleScriptPackInfo(String n, String p) {
        super(n, p);
        this.bundleScripts = new ArrayList<>();
    }
}
