package dev.toapuro.advancedkjs.claasgen.handler;

import dev.toapuro.advancedkjs.claasgen.generator.GeneratedClass;
import dev.toapuro.advancedkjs.coremod.AgentTransformer;
import javassist.CtClass;

import javax.annotation.Nullable;
import java.util.Map;

public class ClassLoaderHandler {
    private static @Nullable AdvancedKJSClassLoader currentClassLoader = null;

    public static void setClassLoader(AdvancedKJSClassLoader classLoader) {
        currentClassLoader = classLoader;
    }

    public static Class<?> defineClass(GeneratedClass generatedClass) {
        if(currentClassLoader == null) return null;

        Map<String, Class<?>> classMap = currentClassLoader.getGenClassLookup();

        CtClass ctClass = generatedClass.ctClass();
        ctClass.defrost();

        if (classMap.containsKey(generatedClass.fqcn())) {

            Class<?> clazz = classMap.get(generatedClass.fqcn());
            AgentTransformer.INSTANCE.redefineClass(clazz, generatedClass.bytes());

            ctClass.freeze();
            return clazz;
        } else {
            return currentClassLoader.defineOrGet(generatedClass);
        }
    }

    @Nullable
    public static AdvancedKJSClassLoader getCurrentClassLoader() {
        return currentClassLoader;
    }
}
