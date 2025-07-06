package dev.toapuro.kubeextra.claasgen;

import dev.latvian.mods.rhino.BaseFunction;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.arguments.MethodParameterTypes;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;

public class KubeMethod {
    private final KubeClass parentClass;
    private final String methodName;
    private final String descriptor;
    private final List<KubeAnnotation> annotations;
    private final CtClass returnType;
    private final MethodParameterTypes parameters;
    private final int modifiers;
    private final BaseFunction implCallback;

    public KubeMethod(KubeClass parentClass, String methodName, String descriptor, CtClass returnType, MethodParameterTypes parameters, int modifiers, BaseFunction implCallback) {
        this.parentClass = parentClass;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.annotations = new ArrayList<>();
        this.returnType = returnType;
        this.parameters = parameters;
        this.modifiers = modifiers;
        this.implCallback = implCallback;
    }

    public void addAnnotation(KubeAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public KubeClass getParentClass() {
        return parentClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public List<KubeAnnotation> getAnnotations() {
        return annotations;
    }

    public CtClass getReturnType() {
        return returnType;
    }

    public MethodParameterTypes getParameters() {
        return parameters;
    }

    public int getModifiers() {
        return modifiers;
    }

    public BaseFunction getImplCallback() {
        return implCallback;
    }
}
