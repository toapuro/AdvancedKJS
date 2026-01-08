package dev.toapuro.advancedkjs.content.kubejs.wrappers;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;

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

    public static Object cast(TypeJS typeJS) {
        Object value = typeJS._getValue();
        Context ctx = ScriptManager.getCurrentContext();
        if (ctx == null) return null;

        TypeWrappers typeWrappers = ctx.getTypeWrappers();
        TypeWrapperFactory<?> wrapperFactory = typeWrappers.getWrapperFactory(typeJS._getTargetClass(), value);
        if (wrapperFactory == null) {
            return null;
        }
        return wrapperFactory.wrap(ctx, value);
    }

    public Class<?> _getTargetClass() {
        return targetClass;
    }

    public boolean _validateTarget(Class<?> targetClass) {
        return this.targetClass == targetClass;
    }

    public Object _getValue() {
        return value;
    }
}
