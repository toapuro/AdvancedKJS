package dev.toapuro.kubeextra.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.parameter.MethodParameterTypes;
import javassist.CtClass;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KubeMethodBuilderJS {
    private final Context context;
    private final String methodName;
    private final String descriptor;
    private final CtClass returnValue;
    private int modifiers;
    private final MethodParameterTypes parameters;
    private final List<KubeAnnotation> annotations;

    public KubeMethodBuilderJS(Context context, String methodName, String descriptor, CtClass returnValue, MethodParameterTypes parameters) {
        this.context = context;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.returnValue = returnValue;
        this.modifiers = 0;
        this.parameters = parameters;
        this.annotations = new ArrayList<>();
    }

    public KubeMethodBuilderJS annotation(KubeAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public KubeMethodBuilderJS annotation(String annotationName, Scriptable annotationParams) {
        KubeAnnotation annotation = KubeAnnotation.fromScriptable(annotationName, annotationParams, context);
        this.annotations.add(annotation);
        return this;
    }

    public KubeMethodBuilderJS priv() {
        this.modifiers = this.modifiers | Modifier.PRIVATE;
        return this;
    }

    public KubeMethodBuilderJS pub() {
        this.modifiers = this.modifiers | Modifier.PUBLIC;
        return this;
    }

    public KubeMethodBuilderJS prot() {
        this.modifiers = this.modifiers | Modifier.PROTECTED;
        return this;
    }

    public KubeMethodBuilderJS statical() {
        this.modifiers = this.modifiers | Modifier.STATIC;
        return this;
    }

    public KubeMethodBuilderJS finalized() {
        this.modifiers = this.modifiers | Modifier.FINAL;
        return this;
    }

    public KubeMethodBuilderJS modifier(int modifier) {
        this.modifiers = this.modifiers | modifier;
        return this;
    }

    public Result body(BaseFunction function) {
        return new Result(methodName, descriptor, parameters, annotations, returnValue, modifiers, function);
    }

    public record Result(String methodName, String descriptor, MethodParameterTypes parameters, List<KubeAnnotation> annotations, CtClass returnValue, int modifiers, BaseFunction function) {
    }
}
