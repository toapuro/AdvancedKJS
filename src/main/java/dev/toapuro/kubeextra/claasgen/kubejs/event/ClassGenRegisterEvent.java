package dev.toapuro.kubeextra.claasgen.kubejs.event;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.toapuro.kubeextra.claasgen.HardcodedConfig;
import dev.toapuro.kubeextra.claasgen.kubejs.builder.KubeClassBuilderJS;

public class ClassGenRegisterEvent extends EventJS {
    public ClassGenRegisterEvent() {
    }

    public KubeClassBuilderJS defineClass(String className) {
        String fqcn = HardcodedConfig.generatedPackage + "." + className;
        return KubeClassBuilderJS.create(ScriptManager.getCurrentContext(), fqcn, className);
    }

    public KubeClassBuilderJS defineClass(String packageName, String className) {
        String fqcn = packageName + "." + className;
        return KubeClassBuilderJS.create(ScriptManager.getCurrentContext(), fqcn, className);
    }
}
