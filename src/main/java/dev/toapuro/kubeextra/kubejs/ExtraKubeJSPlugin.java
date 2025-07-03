package dev.toapuro.kubeextra.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.rhino.Context;
import dev.toapuro.kubeextra.claasgen.KubeClassGenHandler;
import dev.toapuro.kubeextra.claasgen.kubejs.KubeJSImplHandler;
import dev.toapuro.kubeextra.claasgen.kubejs.builder.KubeClassBuilderJS;
import dev.toapuro.kubeextra.claasgen.kubejs.event.ClassGenRegisterEvent;
import dev.toapuro.kubeextra.claasgen.kubejs.event.KubeClassGenEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtraKubeJSPlugin extends KubeJSPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtraKubeJSPlugin.class);

    @Override
    public void registerEvents() {
        KubeClassGenEvents.GROUP.register();
    }

    @Override
    public void init() {
        LOGGER.info("Kube plugin loaded");
    }

    @Override
    public void onServerReload() {
        KubeClassGenHandler classGenHandler = KubeClassGenHandler.INSTANCE;
        classGenHandler.clearPending();
        KubeJSImplHandler.clearClassImplMap();

        KubeClassGenEvents.REGISTER_CLASS_GEN.post(ScriptType.SERVER, new ClassGenRegisterEvent());
        classGenHandler.reApply();
    }
}
