package dev.toapuro.kubeextra.claasgen.kubejs.event;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.toapuro.kubeextra.claasgen.kubejs.builder.KubeClassBuilderJS;

public class ClassGenRegisterEvent extends EventJS {
    public ClassGenRegisterEvent() {
    }

    public KubeClassBuilderJS defineClass(String className) {
        return KubeClassBuilderJS.create(ScriptManager.getCurrentContext(), className);
    }
}
