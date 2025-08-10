package dev.toapuro.advancedkjs.util;

import dev.toapuro.advancedkjs.util.builder.ConstructorAccessBuilder;
import dev.toapuro.advancedkjs.util.builder.MethodAccessBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Optional;

@SuppressWarnings("unused")
public class ReflectionUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Class<?> clazz, Object object, String name) {
        try {
            Field Field = clazz.getDeclaredField(name);
            Field.setAccessible(true);
            return (T) Field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Cant get field {} in {}", name, clazz, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T getStaticFieldValue(Class<?> clazz, String name) {
        return getFieldValue(clazz, null, name);
    }

    public static <T> void setFieldValue(Class<?> clazz, Object object, String name, T value) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Cant get field {} in {}", name, clazz, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> void setStaticFieldValue(Class<?> clazz, String name, T value) {
        setFieldValue(clazz, null, name, value);
    }

    public static <T> MethodAccessBuilder<T> methodInvocation(Class<T> tClass, String methodName) {
        return new MethodAccessBuilder<>(tClass, methodName);
    }

    public static <T> ConstructorAccessBuilder<T> constructor(Class<T> tClass) {
        return new ConstructorAccessBuilder<>(tClass);
    }

    public static Optional<Class<?>> getClass(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
