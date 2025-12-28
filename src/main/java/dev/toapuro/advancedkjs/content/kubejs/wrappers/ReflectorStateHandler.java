package dev.toapuro.advancedkjs.content.kubejs.wrappers;

public class ReflectorStateHandler {
    private static boolean ignoreInaccessible = false;

    public static boolean isIgnoreInaccessible() {
        return ignoreInaccessible;
    }

    public static void setIgnoreInaccessible(boolean ignoreInaccessible) {
        ReflectorStateHandler.ignoreInaccessible = ignoreInaccessible;
    }
}
