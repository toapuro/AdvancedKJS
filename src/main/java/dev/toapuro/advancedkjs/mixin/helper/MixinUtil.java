package dev.toapuro.advancedkjs.mixin.helper;

public class MixinUtil {
    @SuppressWarnings("unchecked")
    public static <T> T castSelf(IMixin<T> obj) {
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T) object;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Class<T> type, Object object) {
        return (T) object;
    }
}
