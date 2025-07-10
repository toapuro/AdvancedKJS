package dev.toapuro.advancedkjs.bytes.claasgen.handler;

import dev.toapuro.advancedkjs.bytes.claasgen.generator.ClassGenerator;
import dev.toapuro.advancedkjs.bytes.claasgen.generated.GeneratedClass;
import dev.toapuro.advancedkjs.bytes.claasgen.generated.GeneratedClassCacheMap;
import dev.toapuro.advancedkjs.bytes.claasgen.construction.GenClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ClassGenContext {
    private final Logger LOGGER = LoggerFactory.getLogger(ClassGenContext.class);
    private final List<GenClass> pendingClasses = new ArrayList<>();
    private GeneratedClassCacheMap classCacheMap = new GeneratedClassCacheMap();

    public ClassGenContext() {
    }

    public void clearCache(ClassGenerator generator) {
        generator.clearCache();
    }

    public void addPendingClass(GenClass genClass) {
        pendingClasses.add(genClass);
    }

    private void generateClasses(ClassGenerator generator) {
        GeneratedClassCacheMap generatedMap = new GeneratedClassCacheMap();
        for (GenClass genClass : pendingClasses) {
            LOGGER.info("Generating Class {}", genClass.getClassName());
            GeneratedClass generatedClass = generator.generateClass(genClass);
            generatedMap.put(generatedClass.fqcn(), generatedClass);
        }
        classCacheMap = generatedMap;
    }

    private void defineClasses(GeneratedClassCacheMap cacheMap) {
        cacheMap.forEach((string, generatedClass) -> {
            Class<?> definedClass = ClassLoaderHandler.defineClass(generatedClass);
            LOGGER.info("Defined {}", definedClass);
        });
    }

    public void apply(ClassGenerator generator) {
        clearCache(generator);
        generateClasses(generator);

        AdvancedKJSClassLoader kubeAdvancedKJSClassLoader = new AdvancedKJSClassLoader(getClass().getClassLoader());
        ClassLoaderHandler.setClassLoader(kubeAdvancedKJSClassLoader);

        defineClasses(classCacheMap);
    }
}
