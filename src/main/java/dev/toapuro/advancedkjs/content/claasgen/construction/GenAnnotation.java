package dev.toapuro.advancedkjs.content.claasgen.construction;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.toapuro.advancedkjs.content.claasgen.helper.AnnotationHelper;
import dev.toapuro.advancedkjs.content.utils.CtClassLookupHandler;
import javassist.CtClass;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenAnnotation {
    private final CtClass annotationClass;
    private final Map<String, Object> arguments;

    public GenAnnotation(CtClass annotationClass, Map<String, Object> arguments) {
        this.annotationClass = annotationClass;
        this.arguments = arguments;
    }

    public GenAnnotation(CtClass annotationClass) {
        this.annotationClass = annotationClass;
        this.arguments = new HashMap<>();
    }

    public static GenAnnotation fromScriptable(String annotationName, Scriptable annotationParams, Context context) {
        GenAnnotation annotation;
        annotation = new GenAnnotation(CtClassLookupHandler.lookupOrMake(annotationName));

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

    public Annotation compileAnnotation(ConstPool cp) {
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
