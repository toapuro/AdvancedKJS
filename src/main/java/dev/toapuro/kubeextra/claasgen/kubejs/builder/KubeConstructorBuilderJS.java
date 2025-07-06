package dev.toapuro.kubeextra.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.kubeextra.claasgen.KubeClass;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.kubeextra.claasgen.arguments.SuperParamArgument;
import dev.toapuro.kubeextra.claasgen.arguments.SuperParamFunctionArgument;
import dev.toapuro.kubeextra.claasgen.kubejs.callback.InstantFunction;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class KubeConstructorBuilderJS extends KubeMethodBuilderJS {
    private final List<SuperParamArgument> paramArguments;

    public KubeConstructorBuilderJS(KubeClass kubeClass, Context context, String methodName, String descriptor, CtClass returnValue, MethodParameterTypes parameters) {
        super(kubeClass, context, methodName, descriptor, returnValue, parameters);
        this.paramArguments = new ArrayList<>();
    }

    public KubeConstructorBuilderJS superCall(String descriptor, Object... arguments) {
        CtClass[] parameterTypes;
        try {
            parameterTypes = Descriptor.getParameterTypes(descriptor, ClassPool.getDefault());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < arguments.length; i++) {
            Object argument = arguments[i];
            CtClass parameterType = parameterTypes[i];
            if (argument instanceof BaseFunction baseFunc) {
                InstantFunction instant = this.kubeClass.addInstantFunction(baseFunc);
                this.paramArguments.add(new SuperParamFunctionArgument(parameterType, instant, i));
            } else {
                InstantFunction instant = this.kubeClass.addInstantFunction(new BaseFunction() {
                    @Override
                    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                        return argument;
                    }
                });
                this.paramArguments.add(new SuperParamFunctionArgument(parameterType, instant, i));
            }
        }

        return this;
    }

    @Override
    public KubeConstructorBuilderJS.Result body(BaseFunction function) {
        KubeMethodBuilderJS.Result result = super.body(function);
        return new Result(
                result.methodName(),
                result.descriptor(),
                result.parameters(),
                result.annotations(),
                result.returnValue(),
                result.modifiers(),
                result.function(),
                paramArguments
        );
    }

    public static class Result extends KubeMethodBuilderJS.Result {
        private final List<SuperParamArgument> superArguments;

        public Result(String methodName, String descriptor, MethodParameterTypes parameters, List<KubeAnnotation> annotations, CtClass returnValue, int modifiers, BaseFunction function, List<SuperParamArgument> superArguments) {
            super(methodName, descriptor, parameters, annotations, returnValue, modifiers, function);
            this.superArguments = superArguments;
        }

        public List<SuperParamArgument> superArguments() {
            return superArguments;
        }
    }
}
