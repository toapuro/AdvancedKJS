package dev.toapuro.kubeextra.claasgen.kubejs;

import dev.latvian.mods.rhino.BaseFunction;
import dev.toapuro.kubeextra.claasgen.KubeClass;
import dev.toapuro.kubeextra.claasgen.KubeMethod;
import dev.toapuro.kubeextra.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.kubeextra.claasgen.arguments.SuperParamArgument;
import javassist.CtClass;

import java.util.List;

public class KubeConstructor extends KubeMethod {
    private final List<SuperParamArgument> superArguments;

    public KubeConstructor(KubeClass parentClass, String descriptor, MethodParameterTypes parameters, int modifiers, BaseFunction implCallback, List<SuperParamArgument> superArguments) {
        super(parentClass, "<init>", descriptor, CtClass.voidType, parameters, modifiers, implCallback);
        this.superArguments = superArguments;
    }

    public List<SuperParamArgument> getSuperArguments() {
        return superArguments;
    }
}
