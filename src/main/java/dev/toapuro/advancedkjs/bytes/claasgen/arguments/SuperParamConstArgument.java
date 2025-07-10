package dev.toapuro.advancedkjs.bytes.claasgen.arguments;

import javassist.CtClass;

public class SuperParamConstArgument extends SuperParamArgument {
    private final Object initialValue;

    public SuperParamConstArgument(CtClass paramClass, ArgType argType, Object initialValue) {
        super(paramClass, argType);
        this.initialValue = initialValue;
    }

    public SuperParamConstArgument(CtClass paramClass, int initialValue) {
        this(paramClass, ArgType.CONST_I, Integer.valueOf(initialValue));
    }

    public SuperParamConstArgument(CtClass paramClass, double initialValue) {
        this(paramClass, ArgType.CONST_D, Double.valueOf(initialValue));
    }

    public SuperParamConstArgument(CtClass paramClass, float initialValue) {
        this(paramClass, ArgType.CONST_F, Float.valueOf(initialValue));
    }

    public SuperParamConstArgument(CtClass paramClass, long initialValue) {
        this(paramClass, ArgType.CONST_L, Long.valueOf(initialValue));
    }

    public Object getInitialValue() {
        return initialValue;
    }
}
