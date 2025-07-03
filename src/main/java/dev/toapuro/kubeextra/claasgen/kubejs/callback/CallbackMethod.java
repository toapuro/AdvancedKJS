package dev.toapuro.kubeextra.claasgen.kubejs.callback;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;

public record CallbackMethod(String methodName, BaseFunction callback) {
    public Object call(Context context, Object instance, Object[] args) {
        return callback.call(context, context.getTopCallScope(), null, new Object[] { instance, args });
    }
}
