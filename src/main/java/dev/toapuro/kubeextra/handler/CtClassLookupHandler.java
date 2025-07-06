package dev.toapuro.kubeextra.handler;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CtClassLookupHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CtClassLookupHandler.class);
    private static Map<String, CtClass> cachedClassMap = new ConcurrentHashMap<>();
    private static ClassPool classPool;

    public static void clearCache() {
        cachedClassMap.clear();
    }

    public static Optional<CtClass> lookup(String name) {
        if (Objects.isNull(classPool)) {
            classPool = ClassPool.getDefault();
        }

        if (cachedClassMap.containsKey(name)) {
            return Optional.of(cachedClassMap.get(name));
        }

        try {
            CtClass ctClass = classPool.get(name);
            cachedClassMap.put(name, ctClass);
            return Optional.of(ctClass);
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    public static CtClass lookupOrMake(String name) {
        if (Objects.isNull(classPool)) {
            classPool = ClassPool.getDefault();
        }

        if(cachedClassMap.containsKey(name)) {
            return cachedClassMap.get(name);
        }

        try {
            CtClass ctClass = classPool.get(name);
            cachedClassMap.put(name, ctClass);
            return ctClass;
        } catch (NotFoundException e) {
            return classPool.makeClass(name);
        }
    }

    public static CtClass lookupOrMake(Class<?> clazz) {
        return lookupOrMake(clazz.getName());
    }
}
