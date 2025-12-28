package dev.toapuro.advancedkjs.mixin.helper;

public class MixinUtil {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T) object;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object, Class<T> type) {
        return (T) object;
    }
}
