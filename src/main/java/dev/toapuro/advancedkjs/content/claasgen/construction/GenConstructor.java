package dev.toapuro.advancedkjs.content.claasgen.construction;

import dev.latvian.mods.rhino.BaseFunction;
import dev.toapuro.advancedkjs.content.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.advancedkjs.content.claasgen.arguments.SuperParamArgument;
import javassist.CtClass;

import java.util.List;

public class GenConstructor extends GenMethod {
    private final List<SuperParamArgument> superArguments;

    public GenConstructor(GenClass parentClass, String descriptor, MethodParameterTypes parameters, int modifiers, BaseFunction implCallback, List<SuperParamArgument> superArguments) {
        super(parentClass, "<init>", descriptor, CtClass.voidType, parameters, modifiers, implCallback);
        this.superArguments = superArguments;
    }

    public List<SuperParamArgument> getSuperArguments() {
        return superArguments;
    }
}
