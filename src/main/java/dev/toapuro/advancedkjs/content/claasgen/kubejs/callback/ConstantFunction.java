package dev.toapuro.advancedkjs.content.claasgen.kubejs.callback;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;

public class ConstantFunction extends BaseFunction {
    private final Object value;

    public ConstantFunction(Object value) {
        this.value = value;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return this.value;
    }
}
