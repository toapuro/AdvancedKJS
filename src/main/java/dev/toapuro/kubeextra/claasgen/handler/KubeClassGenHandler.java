package dev.toapuro.kubeextra.claasgen.handler;

import dev.toapuro.kubeextra.claasgen.KubeClass;
import dev.toapuro.kubeextra.claasgen.generator.ClassGenerator;
import dev.toapuro.kubeextra.claasgen.generator.GeneratedClass;
import dev.toapuro.kubeextra.claasgen.generator.GeneratedClassCacheMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class KubeClassGenHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubeClassGenHandler.class);
    public static KubeClassGenHandler INSTANCE = new KubeClassGenHandler();


    private final List<KubeClass> pendingClasses;
    private final ClassGenerator generator;
    private GeneratedClassCacheMap generatedClassCacheMap;

    public KubeClassGenHandler() {
        this.pendingClasses = new ArrayList<>();
        this.generator = new ClassGenerator();
        this.generatedClassCacheMap = new GeneratedClassCacheMap();
    }

    public void clearCache() {
        generator.clearCache();
    }

    public void clearPending() {
        pendingClasses.clear();
    }

    public void addPendingClass(KubeClass kubeClass) {
        pendingClasses.add(kubeClass);
    }

    public GeneratedClassCacheMap generateAll() {
        GeneratedClassCacheMap generatedMap = new GeneratedClassCacheMap();
        for (KubeClass kubeClass : pendingClasses) {
            LOGGER.info("Generating Class {}", kubeClass.getClassName());
            GeneratedClass generatedClass = generator.generateClass(kubeClass);
            generatedMap.put(generatedClass.fqcn(), generatedClass);
        }
        return generatedMap;
    }

    public void reApply() {
        clearCache();

        generatedClassCacheMap = KubeClassGenHandler.INSTANCE.generateAll();

        KubeExtraClassLoader kubeExtraClassLoader = new KubeExtraClassLoader(getClass().getClassLoader());
        ClassLoaderHandler.setClassLoader(kubeExtraClassLoader);

        generatedClassCacheMap.forEach((string, generatedClass) -> {
            Class<?> definedClass = ClassLoaderHandler.defineClass(generatedClass);
            LOGGER.info("Defined {}", definedClass);
        });
    }

    public GeneratedClassCacheMap getGeneratedCacheMap() {
        return generatedClassCacheMap;
    }
}
