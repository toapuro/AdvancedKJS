package dev.toapuro.advancedkjs.util;

import javax.annotation.Nullable;
import java.util.Optional;

public class CommonUtil {
    @SuppressWarnings("unchecked")
    public static <R> Optional<R> castSafe(Object obj) {
        try {
            return Optional.ofNullable((R) obj);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    public static <R> Optional<R> castSafe(Class<R> clazz, Object obj) {
        return Optional.ofNullable(castNullable(obj));
    }

    @Nullable
    public static <R> R castNullable(Object obj) {
        return CommonUtil.<R>castSafe(obj).orElse(null);
    }

    @Nullable
    public static <R> R castOrThrow(Object obj) {
        return CommonUtil.<R>castSafe(obj).orElseThrow(() -> new ClassCastException(obj.getClass() + " could not be casted"));
    }

    @Nullable
    public static <R> Optional<R> castNullable(Class<R> clazz, Object obj) {
        return castNullable(obj);
    }
}
