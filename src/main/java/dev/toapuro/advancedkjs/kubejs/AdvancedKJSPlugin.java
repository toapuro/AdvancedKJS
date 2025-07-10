package dev.toapuro.advancedkjs.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.toapuro.advancedkjs.bytes.claasgen.GenHandler;
import dev.toapuro.advancedkjs.kubejs.event.ClassGenRegisterEvent;
import dev.toapuro.advancedkjs.kubejs.group.DatagenEventsJS;
import dev.toapuro.advancedkjs.kubejs.group.KubeClassGenEventsJS;
import dev.toapuro.advancedkjs.resolution.TypeJS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancedKJSPlugin extends KubeJSPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedKJSPlugin.class);
    private static final String ISSUE_URL = "https://github.com/toapuro/AdvancedKJS/issues";

    @Override
    public void registerEvents() {
        KubeClassGenEventsJS.GROUP.register();
        DatagenEventsJS.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("Type", TypeJS.class);
    }

    @Override
    public void init() {
        LOGGER.info("Kube plugin loaded");
    }

    @Override
    public void onServerReload() {
        this.initAndApply();
    }

    public void initAndApply() {
        if(getClass().getClassLoader().getResource("javassist") != null) {
            GenHandler.init();

            KubeClassGenEventsJS.REGISTER_CLASS_GEN.post(ScriptType.SERVER, new ClassGenRegisterEvent());

            GenHandler.apply();
        } else {
            throw new RuntimeException("Javassist not loaded. Issue -> " + ISSUE_URL);
        }
    }
}
