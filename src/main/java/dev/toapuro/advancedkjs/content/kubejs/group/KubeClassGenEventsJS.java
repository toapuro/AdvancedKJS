package dev.toapuro.advancedkjs.content.kubejs.group;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.toapuro.advancedkjs.content.kubejs.event.ClassGenRegisterEvent;

public class KubeClassGenEventsJS {
    public static EventGroup GROUP = EventGroup.of("ClassGenEvents");
    public static EventHandler REGISTER_CLASS_GEN = GROUP.common("register", () ->  ClassGenRegisterEvent.class).hasResult();
}
