package dev.toapuro.advancedkjs.content.kubejs.wrappers;

@SuppressWarnings("unused")
public class ReflectorJS {
    public static final String reflectionField = "__unsafe_reflected";
    private final Class<?> staticType;
    private final Object internal;

    public ReflectorJS(Object internal) {
        this.internal = internal;
        this.staticType = internal.getClass();
    }

    public static Object wrap(Object internal) {
        return new ReflectorJS(internal);
    }

    public Object unwrap() {
        return staticType.cast(internal);
    }

    public Class<?> getStaticType() {
        return staticType;
    }

    @Override
    public String toString() {
        return "ReflectedObject " + internal.toString();
    }
}
