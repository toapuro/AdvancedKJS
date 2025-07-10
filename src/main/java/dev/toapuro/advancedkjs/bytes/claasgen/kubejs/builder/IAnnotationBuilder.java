package dev.toapuro.advancedkjs.bytes.claasgen.kubejs.builder;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.advancedkjs.bytes.claasgen.construction.GenAnnotation;
import dev.toapuro.advancedkjs.bytes.common.CtClassLookupHandler;
import javassist.CtClass;

import java.util.HashMap;

public interface IAnnotationBuilder<T extends IAnnotationBuilder<T>> {
    T annotation(GenAnnotation annotation);

    Context getContext();

    default T annotation(String annotationName, Scriptable annotationParams) {
        GenAnnotation annotation = GenAnnotation.fromScriptable(annotationName, annotationParams, getContext());
        return annotation(annotation);
    }

    default T annotation(String annotationName) {
        CtClass ctClass = CtClassLookupHandler.lookupOrMake(annotationName);
        GenAnnotation annotation = new GenAnnotation(ctClass, new HashMap<>());
        return annotation(annotation);
    }
}
