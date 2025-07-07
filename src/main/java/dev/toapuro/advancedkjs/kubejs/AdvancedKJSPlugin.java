package dev.toapuro.advancedkjs.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.toapuro.advancedkjs.claasgen.GenHandler;
import dev.toapuro.advancedkjs.kubejs.event.ClassGenRegisterEvent;
import dev.toapuro.advancedkjs.kubejs.events.KubeClassGenEventsJS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancedKJSPlugin extends KubeJSPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedKJSPlugin.class);

    @Override
    public void registerEvents() {
        KubeClassGenEventsJS.GROUP.register();
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
        GenHandler.init();

        KubeClassGenEventsJS.REGISTER_CLASS_GEN.post(ScriptType.SERVER, new ClassGenRegisterEvent());

        GenHandler.apply();
    }
}
