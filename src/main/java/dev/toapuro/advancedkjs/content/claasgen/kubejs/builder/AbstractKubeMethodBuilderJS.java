package dev.toapuro.advancedkjs.content.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.toapuro.advancedkjs.content.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenAnnotation;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenClass;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class AbstractKubeMethodBuilderJS<BUILDER extends AbstractKubeMethodBuilderJS<BUILDER, RESULT>, RESULT>
        implements IModifierBuilder<BUILDER>, IAnnotationBuilder<BUILDER> {
    protected final GenClass genClass;
    protected final Context context;
    protected final String methodName;
    protected final String descriptor;
    protected final CtClass returnValue;
    protected final MethodParameterTypes parameters;
    protected final List<GenAnnotation> annotations;
    protected int modifiers;

    public AbstractKubeMethodBuilderJS(GenClass genClass, Context context, String methodName, String descriptor, CtClass returnValue, MethodParameterTypes parameters) {
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
    @SuppressWarnings("unchecked")
    public BUILDER annotation(GenAnnotation annotation) {
        this.annotations.add(annotation);
        return (BUILDER) this;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public abstract RESULT body(BaseFunction function);
}
