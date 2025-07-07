package dev.toapuro.advancedkjs.claasgen;

import dev.toapuro.advancedkjs.claasgen.generator.ClassGenerator;
import dev.toapuro.advancedkjs.claasgen.gens.GenClass;
import dev.toapuro.advancedkjs.claasgen.handler.ClassGenContext;
import dev.toapuro.advancedkjs.claasgen.kubejs.KubeJSImplHandler;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.CallbackClass;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.ImplContext;
import dev.toapuro.advancedkjs.handler.CtClassLookupHandler;

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
