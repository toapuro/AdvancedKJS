package dev.toapuro.advancedkjs.claasgen.kubejs.callback;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;

public record CallbackMethod(String name, String descriptor, BaseFunction callback) implements ICallbackFunctionBase {
    @Override
    public Object callFunctionRaw(Context context, Scriptable scope, Object instance, Object[] args) {
        return callback.call(context, context.getTopCallScope(), null, args);
    }
}
