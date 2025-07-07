package dev.toapuro.advancedkjs.claasgen.arguments;

import dev.toapuro.advancedkjs.claasgen.kubejs.callback.InstantFunction;
import javassist.CtClass;

public class SuperParamFunctionArgument extends SuperParamArgument {
    private final InstantFunction instant;
    private final int index;

    public SuperParamFunctionArgument(CtClass paramClass, InstantFunction instant, int index) {
        super(paramClass, ArgType.FUNCTION);
        this.instant = instant;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public InstantFunction getInstant() {
        return instant;
    }
}
