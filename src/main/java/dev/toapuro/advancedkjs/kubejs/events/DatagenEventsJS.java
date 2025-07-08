package dev.toapuro.advancedkjs.kubejs.events;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.toapuro.advancedkjs.kubejs.event.DatagenRecipeRegisterEvent;

public class DatagenEventsJS {
    public static EventGroup GROUP = EventGroup.of("DatagenEvents");
    public static EventHandler DATAGEN_RECIPE = GROUP.server("recipes", () -> DatagenRecipeRegisterEvent.class);
}
