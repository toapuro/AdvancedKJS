package dev.toapuro.advancedkjs.content.kubejs.event;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.toapuro.advancedkjs.config.HardcodedConfig;
import dev.toapuro.advancedkjs.content.claasgen.kubejs.builder.KubeClassBuilderJS;

public class ClassGenRegisterEvent extends EventJS {
    public ClassGenRegisterEvent() {
    }

    public KubeClassBuilderJS defineClass(String className) {
        String fqcn = HardcodedConfig.generatedPackage + "." + className;
        return KubeClassBuilderJS.create(ScriptManager.getCurrentContext(), fqcn, className);
    }
}
