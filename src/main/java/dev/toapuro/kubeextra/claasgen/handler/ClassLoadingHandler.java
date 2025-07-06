package dev.toapuro.kubeextra.claasgen.handler;

import dev.toapuro.kubeextra.claasgen.generator.GeneratedClass;
import dev.toapuro.kubeextra.coremod.AgentTransformer;
import javassist.CtClass;

import javax.annotation.Nullable;
import java.util.Map;

public class ClassLoadingHandler {
    private static @Nullable KubeExtraClassLoader currentClassLoader = null;

    public static void setClassLoader(KubeExtraClassLoader classLoader) {
        currentClassLoader = classLoader;
    }

    public static Class<?> defineClass(GeneratedClass generatedClass) {
        if(currentClassLoader == null) return null;

        Map<String, Class<?>> classMap = currentClassLoader.getClassLookup();

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
    public static KubeExtraClassLoader getCurrentClassLoader() {
        return currentClassLoader;
    }
}
