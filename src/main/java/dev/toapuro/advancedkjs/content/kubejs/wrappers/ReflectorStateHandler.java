package dev.toapuro.advancedkjs.content.kubejs.wrappers;

public class ReflectorStateHandler {
    private static boolean reflected = false;

    public static boolean isReflected() {
        return reflected;
    }

    public static void setReflected(boolean reflected) {
        ReflectorStateHandler.reflected = reflected;
    }
}
