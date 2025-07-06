package dev.toapuro.kubeextra.claasgen.kubejs.builder;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.kubeextra.claasgen.KubeClass;
import dev.toapuro.kubeextra.claasgen.KubeMethod;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.kubeextra.claasgen.handler.KubeClassGenHandler;
import dev.toapuro.kubeextra.claasgen.kubejs.*;
import dev.toapuro.kubeextra.claasgen.kubejs.callback.CallbackClass;
import dev.toapuro.kubeextra.claasgen.kubejs.callback.CallbackMethod;
import dev.toapuro.kubeextra.claasgen.kubejs.callback.InstantFunction;
import dev.toapuro.kubeextra.handler.CtClassLookupHandler;
import dev.toapuro.kubeextra.util.MethodDescriptorParser;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

@SuppressWarnings("unused")
public class KubeClassBuilderJS extends EventJS {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubeClassBuilderJS.class);

    private final KubeClass kubeClass;
    private final Context context;
    private int accessModifiers;
    private int additionalModifiers;

    private KubeClassBuilderJS(KubeClass kubeClass, Context context) {
        this.kubeClass = kubeClass;
        this.context = context;
        this.accessModifiers = Modifier.PRIVATE;
        this.additionalModifiers = 0;
    }

    public static KubeClassBuilderJS create(Context context, String fqcn, String className) {
        return new KubeClassBuilderJS(new KubeClass(fqcn, className), context);
    }

    public KubeClassBuilderJS annotation(KubeAnnotation annotation) {
        this.kubeClass.addAnnotation(annotation);
        return this;
    }

    public KubeClassBuilderJS annotation(String annotationName, Scriptable annotationParams) {
        KubeAnnotation annotation = KubeAnnotation.fromScriptable(annotationName, annotationParams, context);
        this.kubeClass.addAnnotation(annotation);
        return this;
    }

    public KubeClassBuilderJS priv() {
        this.accessModifiers = Modifier.PRIVATE;
        return this;
    }

    public KubeClassBuilderJS pub() {
        this.accessModifiers = Modifier.PUBLIC;
        return this;
    }

    public KubeClassBuilderJS prot() {
        this.accessModifiers = Modifier.PROTECTED;
        return this;
    }

    public KubeClassBuilderJS statical() {
        this.additionalModifiers = this.additionalModifiers | Modifier.STATIC;
        return this;
    }

    public KubeClassBuilderJS finalized() {
        this.additionalModifiers = this.additionalModifiers | Modifier.FINAL;
        return this;
    }

    public KubeClassBuilderJS modifier(int modifier) {
        this.additionalModifiers = this.additionalModifiers | modifier;
        return this;
    }

    public KubeClassBuilderJS extend(String className) {
        CtClass extendClass = CtClassLookupHandler.lookupOrMake(className);
        kubeClass.setSuperClass(extendClass);
        return this;
    }

    public KubeClassBuilderJS implement(String className) {
        CtClass extendClass = CtClassLookupHandler.lookupOrMake(className);
        kubeClass.addImplementsClass(extendClass);
        return this;
    }

    public KubeClassBuilderJS field(String fieldName, String className, Object initialValue, AccessTypeJS accessType, ModifierTypeJS modifierTypeJS) {
        return field(fieldName, className, initialValue, accessType.getModifiers() | modifierTypeJS.getModifiers());
    }

    public KubeClassBuilderJS field(String fieldName, String className, Object initialValue, AccessTypeJS accessType) {
        return field(fieldName, className, initialValue, accessType.getModifiers());
    }

    public KubeClassBuilderJS field(String fieldName, String className, Object initialValue) {
        return field(fieldName, className, initialValue, Modifier.PRIVATE);
    }

    public KubeClassBuilderJS field(String fieldName, String className, AccessTypeJS accessType, ModifierTypeJS modifierTypeJS) {
        return field(fieldName, className, null, accessType.getModifiers() | modifierTypeJS.getModifiers());
    }

    public KubeClassBuilderJS field(String fieldName, String className, AccessTypeJS accessType) {
        return field(fieldName, className, null, accessType.getModifiers());
    }

    public KubeClassBuilderJS field(String fieldName, String className) {
        return field(fieldName, className, null, Modifier.PRIVATE);
    }

    public KubeClassBuilderJS field(String fieldName, String className, Object initialValue, int modifiers) {
        CtClass fieldClass = CtClassLookupHandler.lookupOrMake(className);

        KubeField kubeField = new KubeField(fieldClass, fieldName, initialValue, modifiers);
        kubeClass.addField(kubeField);

        return this;
    }

    public KubeClassBuilderJS constructor(String descriptor, BaseFunction baseFunction) {
        Scriptable scope = context.getTopCallScope();
        MethodParameterTypes methodParameterTypes = MethodParameterTypes.fromDescriptor(descriptor);

        Object resultJs = baseFunction.call(context, scope, scope, new Object[]{
                new KubeConstructorBuilderJS(kubeClass, context, "<init>", descriptor, CtClass.voidType, methodParameterTypes)
        });

        Object resultObj = Context.jsToJava(context, resultJs, KubeConstructorBuilderJS.Result.class);

        if (resultObj instanceof KubeConstructorBuilderJS.Result result) {
            KubeConstructor kubeConstructor = new KubeConstructor(
                    kubeClass, result.descriptor(), result.parameters(), result.modifiers(), result.function(), result.superArguments());
            kubeClass.addConstructor(kubeConstructor);
        } else {
            throw new RuntimeException("Could not build constructor method");
        }

        return this;
    }

    public KubeClassBuilderJS method(String methodNameDescriptor, BaseFunction baseFunction) {
        Scriptable scope = context.getTopCallScope();
        var signature = MethodDescriptorParser.parseMethodWithDescriptor(methodNameDescriptor);
        MethodParameterTypes methodParameterTypes = MethodParameterTypes.fromDescriptor(signature.descriptor());

        CtClass returnType;
        try {
            returnType = Descriptor.getReturnType(signature.descriptor(), ClassPool.getDefault());
        } catch (NotFoundException e) {
            throw new RuntimeException("Return class type not found", e);
        }

        Object resultJs = baseFunction.call(context, scope, scope, new Object[] {
                new KubeMethodBuilderJS(kubeClass, context, signature.methodName(), signature.descriptor(), returnType, methodParameterTypes)
        });

        Object resultObj = Context.jsToJava(context, resultJs, KubeMethodBuilderJS.Result.class);

        if(resultObj instanceof KubeMethodBuilderJS.Result result) {
            KubeMethod kubeMethod = new KubeMethod(
                    kubeClass, result.methodName(), result.descriptor(), result.returnValue(), result.parameters(), result.modifiers(), result.function());
            kubeClass.addMethod(kubeMethod);
        } else {
            throw new RuntimeException("Could not build method");
        }

        return this;
    }

    public void build() {
        kubeClass.setModifiers(additionalModifiers | accessModifiers);

        KubeClassGenHandler.INSTANCE.addPendingClass(kubeClass);

        CallbackClass callback = new CallbackClass(kubeClass.getClassName());
        for (InstantFunction instant : kubeClass.getInstants()) {
            callback.addInstant(instant);
        }

        for (KubeMethod kubeMethod : kubeClass.getMethods()) {
            callback.addMethodCallback(new CallbackMethod(kubeMethod.getMethodName(), kubeMethod.getDescriptor(), kubeMethod.getImplCallback()));
        }

        for (KubeConstructor kubeConstructor : kubeClass.getConstructors()) {
            callback.addMethodCallback(new CallbackMethod(kubeConstructor.getMethodName(), kubeConstructor.getDescriptor(), kubeConstructor.getImplCallback()));
        }
        KubeJSImplHandler.putClassCallback(kubeClass.getFqcn(), callback);
    }
}
