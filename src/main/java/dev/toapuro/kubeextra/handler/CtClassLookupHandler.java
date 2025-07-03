package dev.toapuro.kubeextra.handler;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CtClassLookupHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CtClassLookupHandler.class);
    private static Map<String, CtClass> cachedClassMap;
    private static ClassPool classPool;

    public static CtClass lookup(String name) {
        if (Objects.isNull(classPool)) {
            classPool = ClassPool.getDefault();
        }
        if (Objects.isNull(cachedClassMap)) {
            cachedClassMap = new ConcurrentHashMap<>();
        }

        if(cachedClassMap.containsKey(name)) {
            return cachedClassMap.get(name);
        }

        try {
            CtClass ctClass = classPool.get(name);
            cachedClassMap.put(name, ctClass);
            return ctClass;
        } catch (NotFoundException e) {
            LOGGER.error("Error occurred loading class", e);
            throw new RuntimeException(e);
        }
    }

    public static CtClass lookup(Class<?> clazz) {
        return lookup(clazz.getName());
    }
}
