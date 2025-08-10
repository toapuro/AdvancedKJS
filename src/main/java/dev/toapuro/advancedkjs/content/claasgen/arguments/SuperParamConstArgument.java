package dev.toapuro.advancedkjs.content.claasgen.arguments;

import javassist.CtClass;

public class SuperParamConstArgument extends SuperParamArgument {
    private final Object initialValue;

    public SuperParamConstArgument(CtClass paramClass, ArgType argType, Object initialValue) {
        super(paramClass, argType);
        this.initialValue = initialValue;
    }

    public Object getInitialValue() {
        return initialValue;
    }
}
