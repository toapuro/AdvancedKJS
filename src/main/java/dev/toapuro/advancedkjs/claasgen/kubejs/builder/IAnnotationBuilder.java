package dev.toapuro.advancedkjs.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.advancedkjs.claasgen.annotation.KubeAnnotation;
import dev.toapuro.advancedkjs.handler.CtClassLookupHandler;
import javassist.CtClass;

import java.util.HashMap;

public interface IAnnotationBuilder<T extends IAnnotationBuilder<T>> {
    T annotation(KubeAnnotation annotation);

    Context getContext();

    default T annotation(String annotationName, Scriptable annotationParams) {
        KubeAnnotation annotation = KubeAnnotation.fromScriptable(annotationName, annotationParams, getContext());
        return annotation(annotation);
    }

    default T annotation(String annotationName) {
        CtClass ctClass = CtClassLookupHandler.lookupOrMake(annotationName);
        KubeAnnotation annotation = new KubeAnnotation(ctClass, new HashMap<>());
        return annotation(annotation);
    }
}
