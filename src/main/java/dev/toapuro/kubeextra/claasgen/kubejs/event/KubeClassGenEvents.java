package dev.toapuro.kubeextra.claasgen.kubejs.event;

import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public class KubeClassGenEvents {
    public static EventGroup GROUP = EventGroup.of("ClassGenEvents");
    public static EventHandler REGISTER_CLASS_GEN = GROUP.common("register", () ->  ClassGenRegisterEvent.class).hasResult();

    public static void REGISTER_CLASS_GEN(ClassGenRegisterEvent event) throws EventExit {
    }
}
