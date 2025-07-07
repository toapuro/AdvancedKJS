package dev.toapuro.advancedkjs.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.toapuro.advancedkjs.claasgen.annotation.KubeAnnotation;
import dev.toapuro.advancedkjs.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.advancedkjs.claasgen.gens.GenClass;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class KubeMethodBuilderJS implements IModifierBuilder<KubeMethodBuilderJS>, IAnnotationBuilder<KubeMethodBuilderJS> {
    protected final GenClass genClass;
    protected final Context context;
    protected final String methodName;
    protected final String descriptor;
    protected final CtClass returnValue;
    protected final MethodParameterTypes parameters;
    protected final List<KubeAnnotation> annotations;
    private int modifiers;

    public KubeMethodBuilderJS(GenClass genClass, Context context, String methodName, String descriptor, CtClass returnValue, MethodParameterTypes parameters) {
        this.genClass = genClass;
        this.context = context;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.returnValue = returnValue;
        this.modifiers = 0;
        this.parameters = parameters;
        this.annotations = new ArrayList<>();
    }

    @Override
    public void applyModifier(int modifier) {
        this.modifiers |= modifier;
    }

    @Override
    public KubeMethodBuilderJS annotation(KubeAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public Result body(BaseFunction function) {
        return new Result(methodName, descriptor, parameters, annotations, returnValue, modifiers, function);
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
