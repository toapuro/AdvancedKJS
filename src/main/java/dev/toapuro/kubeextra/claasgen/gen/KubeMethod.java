package dev.toapuro.kubeextra.claasgen.gen;

import dev.latvian.mods.rhino.BaseFunction;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.parameter.MethodParameterTypes;
import javassist.CtClass;
import javassist.bytecode.annotation.Annotation;

import java.util.List;

public record KubeMethod(KubeClass parentClass, String methodName, List<KubeAnnotation> annotations,
                         CtClass returnType, MethodParameterTypes parameters, int modifiers, BaseFunction implCallback) {
}
