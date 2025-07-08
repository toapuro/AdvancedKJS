package dev.toapuro.advancedkjs.resolution;

@SuppressWarnings("unused")
public class TypeJS {
    private final Class<?> targetClass;
    private final Object value;

    public TypeJS(Class<?> targetClass, Object value) {
        this.targetClass = targetClass;
        this.value = value;
    }

    public static TypeJS as(Class<?> targetClass, Object value) {
        return new TypeJS(targetClass, value);
    }

    public boolean validateTarget(Class<?> targetClass) {
        return this.targetClass == targetClass;
    }

    public Object getValue() {
        return value;
    }
}
