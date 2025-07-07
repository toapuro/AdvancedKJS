package dev.toapuro.advancedkjs.claasgen.kubejs.builder;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.advancedkjs.claasgen.GenHandler;
import dev.toapuro.advancedkjs.claasgen.annotation.KubeAnnotation;
import dev.toapuro.advancedkjs.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.advancedkjs.claasgen.gens.GenClass;
import dev.toapuro.advancedkjs.claasgen.gens.GenConstructor;
import dev.toapuro.advancedkjs.claasgen.gens.GenField;
import dev.toapuro.advancedkjs.claasgen.gens.GenMethod;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.CallbackClass;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.CallbackMethod;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.ConstantFunction;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.InstantFunction;
import dev.toapuro.advancedkjs.handler.CtClassLookupHandler;
import dev.toapuro.advancedkjs.util.MethodDescriptorParser;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class KubeClassBuilderJS extends EventJS implements IModifierBuilder<KubeClassBuilderJS>, IAnnotationBuilder<KubeClassBuilderJS> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubeClassBuilderJS.class);

    private final GenClass genClass;
    private final Context context;
    private int modifiers;

    private KubeClassBuilderJS(GenClass genClass, Context context) {
        this.genClass = genClass;
        this.context = context;
        this.modifiers = 0;
    }

    public static KubeClassBuilderJS create(Context context, String fqcn, String className) {
        return new KubeClassBuilderJS(new GenClass(fqcn, className), context);
    }

    public KubeClassBuilderJS annotation(KubeAnnotation annotation) {
        this.genClass.addAnnotation(annotation);
        return this;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void applyModifier(int modifier) {
        this.modifiers |= modifier;
    }

    public KubeClassBuilderJS extend(String className) {
        CtClass extendClass = CtClassLookupHandler.lookupOrMake(className);
        genClass.setSuperClass(extendClass);
        return this;
    }

    public KubeClassBuilderJS implement(String className) {
        CtClass extendClass = CtClassLookupHandler.lookupOrMake(className);
        genClass.addImplementsClass(extendClass);
        return this;
    }

    public KubeClassBuilderJS field(String fieldName, String fieldType, BaseFunction buildingFunction) {
        Scriptable scope = context.getTopCallScope();

        Object resultJs = buildingFunction.call(context, scope, null, new Object[]{
                KubeFieldBuilderJS.create(fieldName, genClass, context)
        });

        Object resultObj = Context.jsToJava(context, resultJs, KubeFieldBuilderJS.Result.class);

        if (resultObj instanceof KubeFieldBuilderJS.Result result) {
            CtClass fieldClass = CtClassLookupHandler.lookupOrMake(fieldType);

            GenField genField = new GenField(fieldClass, fieldName, result.getFunction(), modifiers);

            for (KubeAnnotation annotation : result.getAnnotations()) {
                genField.addAnnotation(annotation);
            }

            genClass.addField(genField);

            return this;
        } else {
            throw new RuntimeException("Could not build field");
        }
    }

    public KubeClassBuilderJS field(String fieldName, String fieldType) {
        return field(fieldName, fieldType, null, 0);
    }

    public KubeClassBuilderJS field(String fieldName, String fieldType, Object initialObj) {
        return field(fieldName, fieldType, initialObj, 0);
    }

    public KubeClassBuilderJS field(String fieldName, String fieldType, int modifiers) {
        return field(fieldName, fieldType, null, modifiers);
    }

    public KubeClassBuilderJS field(String fieldName, String fieldType, @Nullable Object initialObj, int modifiers) {
        CtClass fieldClass = CtClassLookupHandler.lookupOrMake(fieldType);

        Object initialFieldValue = null;

        if (initialObj instanceof BaseFunction baseFunction) {
            initialFieldValue = genClass.pushInstantFunction(baseFunction);
        } else if (initialObj != null) {
            initialFieldValue = genClass.pushInstantFunction(new ConstantFunction(initialObj));
        }

        GenField genField = new GenField(fieldClass, fieldName, initialFieldValue, modifiers);

        genClass.addField(genField);
        return this;
    }

    public KubeClassBuilderJS constructor(String descriptor, BaseFunction buildingFunction) {
        Scriptable scope = context.getTopCallScope();
        MethodParameterTypes methodParameterTypes = MethodParameterTypes.fromDescriptor(descriptor);

        Object resultJs = buildingFunction.call(context, scope, scope, new Object[]{
                KubeConstructorBuilderJS.create(genClass, context, "<init>", descriptor, CtClass.voidType, methodParameterTypes)
        });

        Object resultObj = Context.jsToJava(context, resultJs, KubeConstructorBuilderJS.Result.class);

        if (resultObj instanceof KubeConstructorBuilderJS.Result result) {
            GenConstructor kubeConstructor = new GenConstructor(
                    genClass, result.descriptor(), result.parameters(), result.modifiers(), result.function(), result.superArguments());
            genClass.addConstructor(kubeConstructor);
        } else {
            throw new RuntimeException("Could not build constructor method");
        }

        return this;
    }

    public KubeClassBuilderJS method(String methodNameDescriptor, BaseFunction buildingFunction) {
        Scriptable scope = context.getTopCallScope();
        var signature = MethodDescriptorParser.parseMethodWithDescriptor(methodNameDescriptor);
        MethodParameterTypes methodParameterTypes = MethodParameterTypes.fromDescriptor(signature.descriptor());

        CtClass returnType;
        try {
            returnType = Descriptor.getReturnType(signature.descriptor(), ClassPool.getDefault());
        } catch (NotFoundException e) {
            throw new RuntimeException("Return class type not found", e);
        }

        Object resultJs = buildingFunction.call(context, scope, scope, new Object[]{
                new KubeMethodBuilderJS(genClass, context, signature.methodName(), signature.descriptor(), returnType, methodParameterTypes)
        });

        Object resultObj = Context.jsToJava(context, resultJs, KubeMethodBuilderJS.Result.class);

        if (resultObj instanceof KubeMethodBuilderJS.Result result) {
            GenMethod genMethod = new GenMethod(
                    genClass, result.methodName(), result.descriptor(), result.returnValue(), result.parameters(), result.modifiers(), result.function());
            genClass.addMethod(genMethod);
        } else {
            throw new RuntimeException("Could not build method");
        }

        return this;
    }

    public void build() {
        genClass.setModifiers(modifiers);

        GenHandler.addPendingClass(genClass);

        CallbackClass callback = new CallbackClass(genClass.getClassName());
        for (InstantFunction instant : genClass.getInstants()) {
            callback.addInstant(instant);
        }

        for (GenMethod genMethod : genClass.getMethods()) {
            callback.addMethodCallback(new CallbackMethod(genMethod.getMethodName(), genMethod.getDescriptor(), genMethod.getImplCallback()));
        }

        for (GenConstructor kubeConstructor : genClass.getConstructors()) {
            callback.addMethodCallback(new CallbackMethod(kubeConstructor.getMethodName(), kubeConstructor.getDescriptor(), kubeConstructor.getImplCallback()));
        }

        GenHandler.addClassCallback(genClass.getFqcn(), callback);
    }
}
