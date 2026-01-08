package dev.toapuro.advancedkjs.content.classjs;

import dev.toapuro.advancedkjs.api.compat.ICompat;
import pelemenguin.classjs.ClassJS;

/**
 * {@link pelemenguin.classjs.ClassJS}
 */
public class ClassJSCompat implements ICompat {

    public ClassJSCompat() {
    }

    @Override
    public String[] getRequiredMods() {
        return new String[]{
                ClassJS.MOD_ID
        };
    }
}
