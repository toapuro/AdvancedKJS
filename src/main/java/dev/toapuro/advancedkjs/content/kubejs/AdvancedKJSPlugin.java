package dev.toapuro.advancedkjs.content.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import dev.toapuro.advancedkjs.content.kubejs.group.DatagenEventsJS;
import dev.toapuro.advancedkjs.content.kubejs.wrap.JavaWrapperFactories;
import dev.toapuro.advancedkjs.content.kubejs.wrappers.ReflectorJS;
import dev.toapuro.advancedkjs.content.kubejs.wrappers.TypeJS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancedKJSPlugin extends KubeJSPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedKJSPlugin.class);

    @Override
    public void registerEvents() {
        DatagenEventsJS.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("Type", TypeJS.class);
        event.add("Reflector", ReflectorJS.class);
    }

    @Override
    public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
        JavaWrapperFactories.register((target, from, event) -> {
            Context context = ScriptManager.getCurrentContext();
            if (context == null) {
                return;
            }
            Object fromObj = Context.jsToJava(context, from, Object.class);
            if (fromObj instanceof TypeJS typeJS) {
                if (typeJS._validateTarget(target)) {
                    event.setValue((cx, ignored) -> typeJS._getValue());
                } else {
                    event.setValue(null);
                }
            }
        });

        JavaWrapperFactories.register((target, from, event) -> {
            Context context = ScriptManager.getCurrentContext();
            if (context == null) {
                return;
            }
            Object fromObj = Context.jsToJava(context, from, Object.class);
            if (fromObj instanceof ReflectorJS reflectorJS) {
                event.setValue((cx, ignored) -> reflectorJS.unwrap());
            }
        });
    }

    @Override
    public void init() {
        LOGGER.info("Kube plugin loaded");
    }
}
