package dev.toapuro.advancedkjs.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.toapuro.advancedkjs.claasgen.annotation.KubeAnnotation;
import dev.toapuro.advancedkjs.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.advancedkjs.claasgen.arguments.SuperParamArgument;
import dev.toapuro.advancedkjs.claasgen.arguments.SuperParamFunctionArgument;
import dev.toapuro.advancedkjs.claasgen.gens.GenClass;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.ConstantFunction;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.InstantFunction;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class KubeConstructorBuilderJS extends KubeMethodBuilderJS {
    private final List<SuperParamArgument> paramArguments;

    private KubeConstructorBuilderJS(GenClass genClass, Context context, String methodName, String descriptor, CtClass returnValue, MethodParameterTypes parameters) {
        super(genClass, context, methodName, descriptor, returnValue, parameters);
        this.paramArguments = new ArrayList<>();
    }

    public static KubeConstructorBuilderJS create(GenClass genClass, Context context, String methodName, String descriptor, CtClass returnValue, MethodParameterTypes parameters) {
        return new KubeConstructorBuilderJS(genClass, context, methodName, descriptor, returnValue, parameters);
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
                InstantFunction instant = this.genClass.pushInstantFunction(baseFunc);
                this.paramArguments.add(new SuperParamFunctionArgument(parameterType, instant, i));
            } else {
                InstantFunction instant = this.genClass.pushInstantFunction(new ConstantFunction(argument));
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
