package dev.toapuro.kubeextra.claasgen.kubejs.builder;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.kubeextra.claasgen.KubeClass;
import dev.toapuro.kubeextra.claasgen.KubeMethod;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.handler.KubeClassGenHandler;
import dev.toapuro.kubeextra.claasgen.kubejs.KubeJSImplHandler;
import dev.toapuro.kubeextra.claasgen.kubejs.callback.CallbackClass;
import dev.toapuro.kubeextra.claasgen.kubejs.callback.CallbackMethod;
import dev.toapuro.kubeextra.claasgen.parameter.MethodParameterTypes;
import dev.toapuro.kubeextra.util.MethodDescriptorParser;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

public class KubeClassBuilderJS extends EventJS {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubeClassBuilderJS.class);

    private final KubeClass kubeClass;
    private final Context context;
    private int modifiers;

    private KubeClassBuilderJS(KubeClass kubeClass, Context context) {
        this.kubeClass = kubeClass;
        this.context = context;
        this.modifiers = 0;
    }

    public static KubeClassBuilderJS create(Context context, String className) {
        return new KubeClassBuilderJS(new KubeClass(className), context);
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



    public KubeClassBuilderJS emptyConstructor() {
        this.kubeClass.setHasEmptyConstructor(true);
        return this;
    }

    public KubeClassBuilderJS priv() {
        this.modifiers = this.modifiers | Modifier.PRIVATE;
        return this;
    }

    public KubeClassBuilderJS pub() {
        this.modifiers = this.modifiers | Modifier.PUBLIC;
        return this;
    }

    public KubeClassBuilderJS prot() {
        this.modifiers = this.modifiers | Modifier.PROTECTED;
        return this;
    }

    public KubeClassBuilderJS statical() {
        this.modifiers = this.modifiers | Modifier.STATIC;
        return this;
    }

    public KubeClassBuilderJS finalized() {
        this.modifiers = this.modifiers | Modifier.FINAL;
        return this;
    }

    public KubeClassBuilderJS modifier(int modifier) {
        this.modifiers = this.modifiers | modifier;
        return this;
    }

    public KubeClassBuilderJS method(String methodName, BaseFunction baseFunction) {
        Scriptable scope = context.getTopCallScope();
        var signature = MethodDescriptorParser.parseMethodWithDescriptor(methodName);
        MethodParameterTypes methodParameterTypes = MethodParameterTypes.fromDescriptor(signature.descriptor());

        CtClass returnType;
        try {
            returnType = Descriptor.getReturnType(signature.descriptor(), ClassPool.getDefault());
        } catch (NotFoundException e) {
            throw new RuntimeException("Return class type not found", e);
        }

        Object resultJs = baseFunction.call(context, scope, scope, new Object[] {
                new KubeMethodBuilderJS(context, signature.methodName(), signature.descriptor(), returnType, methodParameterTypes)
        });

        Object resultObj = Context.jsToJava(context, resultJs, KubeMethodBuilderJS.Result.class);
        if(resultObj instanceof KubeMethodBuilderJS.Result result) {
            KubeMethod kubeMethod = new KubeMethod(
                    kubeClass, result.methodName(), result.annotations(), result.returnValue(), result.parameters(), result.modifiers(), result.function());
            kubeClass.addMethod(kubeMethod);
        } else {
            throw new RuntimeException("Could not build method");
        }

        return this;
    }

    public void build() {
        KubeClassGenHandler.INSTANCE.addPendingClass(kubeClass);

        CallbackClass callback = new CallbackClass(kubeClass.getClassName(), kubeClass.getMethods().stream()
                .map(kubeMethod -> new CallbackMethod(kubeMethod.methodName(), kubeMethod.implCallback()))
                .toList());
        KubeJSImplHandler.addClassImpl(kubeClass.getClassName(), callback);
    }
}
