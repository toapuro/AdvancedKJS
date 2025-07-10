package dev.toapuro.advancedkjs.bytes.claasgen.kubejs.callback;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;

public record InstantFunction(String className, int instantId, BaseFunction callback) implements ICallbackFunctionBase {
    @Override
    public Object callFunctionRaw(Context context, Scriptable scope, Object instance, Object[] args) {
        return callback.call(context, context.getTopCallScope(), null, args);
    }
}
