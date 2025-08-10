package dev.toapuro.advancedkjs.content.kubejs.wrappers;

public class ReflectorStateHelper {
    private static boolean reflected = false;

    public static boolean isReflected() {
        return reflected;
    }

    public static void setReflected(boolean reflected) {
        ReflectorStateHelper.reflected = reflected;
    }
}
