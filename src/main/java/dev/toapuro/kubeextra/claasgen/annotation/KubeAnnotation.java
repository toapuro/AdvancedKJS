package dev.toapuro.kubeextra.claasgen.annotation;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;

import java.util.*;

public class KubeAnnotation {
    private final CtClass annotationClass;
    private final Map<String, Object> arguments;

    public KubeAnnotation(CtClass annotationClass, Map<String, Object> arguments) {
        this.annotationClass = annotationClass;
        this.arguments = arguments;
    }

    public KubeAnnotation(CtClass annotationClass) {
        this.annotationClass = annotationClass;
        this.arguments = new HashMap<>();
    }

    public static KubeAnnotation fromScriptable(String annotationName, Scriptable annotationParams, Context context) {
        KubeAnnotation annotation;
        try {
            annotation = new KubeAnnotation(ClassPool.getDefault().getCtClass(annotationName));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        List<String> propKeys = Arrays.stream(ScriptableObject.getPropertyIds(context, annotationParams))
                .map(Object::toString).toList();
        for (String propKey : propKeys) {
            Object property = ScriptableObject.getProperty(annotationParams, propKey, context);
            annotation.putArg(propKey, property);
        }
        return annotation;
    }

    public void putArg(String key, Object object) {
        arguments.put(key, object);
    }

    public Annotation buildAnnotation(ConstPool cp) {
        Annotation annotation = new Annotation(annotationClass.getName(), cp);
        arguments.forEach((key, value) -> {
            MemberValue memberValue = AnnotationHelper.getMemberValue(value, cp);
            annotation.addMemberValue(key, memberValue);
        });

        return annotation;
    }

    public CtClass annotationClass() {
        return annotationClass;
    }

    public Map<String, Object> arguments() {
        return arguments;
    }
}
