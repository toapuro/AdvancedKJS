package dev.toapuro.advancedkjs.api.compat;

import net.minecraftforge.fml.ModList;

import java.util.Arrays;

public interface ICompat {

    String[] getRequiredMods();

    default boolean isEnabled() {
        return Arrays.stream(getRequiredMods())
                .allMatch(modid -> ModList.get().isLoaded(modid));
    }
}
