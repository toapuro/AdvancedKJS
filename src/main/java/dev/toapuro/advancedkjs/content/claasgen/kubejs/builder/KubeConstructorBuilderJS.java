package dev.toapuro.advancedkjs.content.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.toapuro.advancedkjs.content.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.advancedkjs.content.claasgen.arguments.SuperParamArgument;
import dev.toapuro.advancedkjs.content.claasgen.arguments.SuperParamFunctionArgument;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenAnnotation;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenClass;
import dev.toapuro.advancedkjs.content.claasgen.kubejs.callback.ConstantFunction;
import dev.toapuro.advancedkjs.content.claasgen.kubejs.callback.InstantFunction;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class KubeConstructorBuilderJS extends AbstractKubeMethodBuilderJS<KubeConstructorBuilderJS, KubeConstructorBuilderJS.Result> {
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
        return new Result(
                methodName,
                descriptor,
                parameters,
                annotations,
                returnValue,
                modifiers,
                function,
                paramArguments
        );
    }

    public record Result(String methodName, String descriptor, MethodParameterTypes parameters,
                         List<GenAnnotation> annotations,
                         CtClass returnValue, int modifiers, BaseFunction function,
                         List<SuperParamArgument> superArguments) {
    }
}
