package dev.toapuro.kubeextra.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.toapuro.kubeextra.claasgen.handler.KubeClassGenHandler;
import dev.toapuro.kubeextra.claasgen.kubejs.KubeJSImplHandler;
import dev.toapuro.kubeextra.claasgen.kubejs.event.ClassGenRegisterEvent;
import dev.toapuro.kubeextra.claasgen.kubejs.event.KubeClassGenEvents;
import dev.toapuro.kubeextra.handler.CtClassLookupHandler;
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
        resetAndApply();
    }

    public void initAll() {
        // TODO: もうちょいスマートにしたい
        KubeClassGenHandler classGenHandler = KubeClassGenHandler.INSTANCE;
        classGenHandler.clearPending();
        KubeJSImplHandler.clearImplMap();
        CtClassLookupHandler.clearCache();
    }

    public void resetAndApply() {
        KubeClassGenHandler classGenHandler = KubeClassGenHandler.INSTANCE;
        initAll();

        KubeClassGenEvents.REGISTER_CLASS_GEN.post(ScriptType.SERVER, new ClassGenRegisterEvent());
        classGenHandler.reApply();
    }
}
