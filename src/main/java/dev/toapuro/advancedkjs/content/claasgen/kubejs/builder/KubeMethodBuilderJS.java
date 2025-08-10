package dev.toapuro.advancedkjs.content.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.toapuro.advancedkjs.content.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenAnnotation;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenClass;
import javassist.CtClass;

import java.util.List;

@SuppressWarnings("unused")
public class KubeMethodBuilderJS extends AbstractKubeMethodBuilderJS<KubeMethodBuilderJS, KubeMethodBuilderJS.Result>
        implements IModifierBuilder<KubeMethodBuilderJS>, IAnnotationBuilder<KubeMethodBuilderJS> {

    public KubeMethodBuilderJS(GenClass genClass, Context context, String methodName, String descriptor, CtClass returnValue, MethodParameterTypes parameters) {
        super(genClass, context, methodName, descriptor, returnValue, parameters);
    }

    public Result body(BaseFunction function) {
        return new Result(
                methodName,
                descriptor,
                parameters,
                annotations,
                returnValue,
                modifiers,
                function
        );
    }

    public record Result(String methodName, String descriptor, MethodParameterTypes parameters,
                         List<GenAnnotation> annotations, CtClass returnValue, int modifiers, BaseFunction function) {
    }
}
