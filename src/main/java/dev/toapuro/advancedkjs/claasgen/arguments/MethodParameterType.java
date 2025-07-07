package dev.toapuro.advancedkjs.claasgen.arguments;

import javassist.CtClass;

public class MethodParameterType {
    enum ValueType {
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        CHAR,
        BYTE,
        SHORT,
        OBJECT
    }

    private final ValueType type;
    private final CtClass ctClass;

    private static ValueType parseType(CtClass clazz) {
        if (clazz == CtClass.intType) {
            return ValueType.INT;
        } else if (clazz == CtClass.longType) {
            return ValueType.LONG;
        } else if (clazz == CtClass.floatType) {
            return ValueType.FLOAT;
        } else if (clazz == CtClass.doubleType) {
            return ValueType.DOUBLE;
        } else if (clazz == CtClass.booleanType) {
            return ValueType.BOOLEAN;
        } else if (clazz == CtClass.charType) {
            return ValueType.CHAR;
        } else if (clazz == CtClass.byteType) {
            return ValueType.BYTE;
        } else if (clazz == CtClass.shortType) {
            return ValueType.SHORT;
        } else {
            return ValueType.OBJECT;
        }
    }

    public MethodParameterType(CtClass ctClass) {
        this.type = parseType(ctClass);
        this.ctClass = ctClass;
    }

    public CtClass getCtClassParam() {
        return switch (type) {
            case INT -> CtClass.intType;
            case LONG -> CtClass.longType;
            case FLOAT -> CtClass.floatType;
            case DOUBLE -> CtClass.doubleType;
            case BOOLEAN -> CtClass.booleanType;
            case CHAR -> CtClass.charType;
            case BYTE -> CtClass.byteType;
            case SHORT -> CtClass.shortType;
            case OBJECT -> ctClass;
        };
    }

    public CtClass getCtClass() {
        return ctClass;
    }
}
