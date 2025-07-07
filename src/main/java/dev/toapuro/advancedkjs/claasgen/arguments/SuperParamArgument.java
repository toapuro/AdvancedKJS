package dev.toapuro.advancedkjs.claasgen.arguments;

import javassist.CtClass;

public class SuperParamArgument {
    private final CtClass paramClass;
    private final ArgType argType;

    public SuperParamArgument(CtClass paramClass, ArgType argType) {
        this.paramClass = paramClass;
        this.argType = argType;
    }

    public CtClass getParamClass() {
        return paramClass;
    }

    public ArgType getArgType() {
        return argType;
    }

    public enum ArgType {
        CONST_I,
        CONST_D,
        CONST_F,
        CONST_L,
        FUNCTION
    }
}
