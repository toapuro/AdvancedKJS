package dev.toapuro.kubeextra.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.kubeextra.claasgen.KubeClass;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.arguments.MethodParameterTypes;
import javassist.CtClass;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class KubeMethodBuilderJS {
    protected final KubeClass kubeClass;
    protected final Context context;
    protected final String methodName;
    protected final String descriptor;
    protected final CtClass returnValue;
    protected final MethodParameterTypes parameters;
    protected final List<KubeAnnotation> annotations;
    protected int additionalModifiers;
    private int accessModifiers;

    public KubeMethodBuilderJS(KubeClass kubeClass, Context context, String methodName, String descriptor, CtClass returnValue, MethodParameterTypes parameters) {
        this.kubeClass = kubeClass;
        this.context = context;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.returnValue = returnValue;
        this.accessModifiers = 0;
        this.additionalModifiers = 0;
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
        this.accessModifiers = Modifier.PRIVATE;
        return this;
    }

    public KubeMethodBuilderJS pub() {
        this.accessModifiers = Modifier.PUBLIC;
        return this;
    }

    public KubeMethodBuilderJS prot() {
        this.accessModifiers = Modifier.PROTECTED;
        return this;
    }

    public KubeMethodBuilderJS statical() {
        this.additionalModifiers = this.additionalModifiers | Modifier.STATIC;
        return this;
    }

    public KubeMethodBuilderJS finalized() {
        this.additionalModifiers = this.additionalModifiers | Modifier.FINAL;
        return this;
    }

    public KubeMethodBuilderJS modifier(int modifier) {
        this.additionalModifiers = this.additionalModifiers | modifier;
        return this;
    }

    public Result body(BaseFunction function) {
        return new Result(methodName, descriptor, parameters, annotations, returnValue, additionalModifiers | accessModifiers, function);
    }

    public static class Result {
        private final String methodName;
        private final String descriptor;
        private final MethodParameterTypes parameters;
        private final List<KubeAnnotation> annotations;
        private final CtClass returnValue;
        private final int modifiers;
        private final BaseFunction function;

        public Result(String methodName, String descriptor, MethodParameterTypes parameters, List<KubeAnnotation> annotations, CtClass returnValue, int modifiers, BaseFunction function) {
            this.methodName = methodName;
            this.descriptor = descriptor;
            this.parameters = parameters;
            this.annotations = annotations;
            this.returnValue = returnValue;
            this.modifiers = modifiers;
            this.function = function;
        }

        public String methodName() {
            return methodName;
        }

        public String descriptor() {
            return descriptor;
        }

        public MethodParameterTypes parameters() {
            return parameters;
        }

        public List<KubeAnnotation> annotations() {
            return annotations;
        }

        public CtClass returnValue() {
            return returnValue;
        }

        public int modifiers() {
            return modifiers;
        }

        public BaseFunction function() {
            return function;
        }
    }
}
