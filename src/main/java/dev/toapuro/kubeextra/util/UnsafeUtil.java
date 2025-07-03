package dev.toapuro.kubeextra.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnsafeUtil.class);
    private static Unsafe UNSAFE_CACHE = null;

    public UnsafeUtil() {
    }

    public static Unsafe getUnsafe() {
        if (UNSAFE_CACHE != null) {
            return UNSAFE_CACHE;
        }
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE_CACHE = (Unsafe)theUnsafe.get(null);
            return UNSAFE_CACHE;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Cant get the Unsafe");
            throw new RuntimeException(e);
        }
    }
}
