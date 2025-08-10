package dev.toapuro.advancedkjs.content.claasgen;

import dev.toapuro.advancedkjs.content.claasgen.construction.GenClass;
import dev.toapuro.advancedkjs.content.claasgen.generator.ClassGenerator;
import dev.toapuro.advancedkjs.content.claasgen.handler.ClassGenContext;
import dev.toapuro.advancedkjs.content.claasgen.kubejs.KubeJSImplHandler;
import dev.toapuro.advancedkjs.content.claasgen.kubejs.callback.CallbackClass;
import dev.toapuro.advancedkjs.content.claasgen.kubejs.callback.ImplContext;
import dev.toapuro.advancedkjs.content.utils.CtClassLookupHandler;

public class GenHandler {
    private static final ClassGenerator generator = new ClassGenerator();
    private static ClassGenContext context;
    private static ImplContext implContext;

    public static void addPendingClass(GenClass genClass) {
        context.addPendingClass(genClass);
    }

    public static void apply() {
        context.apply(generator);
        KubeJSImplHandler.setImplContext(implContext);
    }

    public static void init() {
        CtClassLookupHandler.clearCache();
        context = new ClassGenContext();
        implContext = new ImplContext();
    }

    public static void addClassCallback(String className, CallbackClass callbackClass) {
        implContext.putCallback(className, callbackClass);
    }
}
