package dev.toapuro.advancedkjs.claasgen.handler;

import dev.toapuro.advancedkjs.claasgen.generator.GeneratedClass;

import java.util.HashMap;
import java.util.Map;

public class AdvancedKJSClassLoader extends ClassLoader {
    private Map<String, GeneratedClass> generatedClassMap;
    private Map<String, Class<?>> classLookup;

    public AdvancedKJSClassLoader(ClassLoader parent) {
        super(parent);
        this.classLookup = new HashMap<>();
        this.generatedClassMap = new HashMap<>();
    }

    public final Class<?> defineOrGet(GeneratedClass generatedClass) throws ClassFormatError
    {
        if(classLookup.containsKey(generatedClass.fqcn())) {
            return classLookup.get(generatedClass.fqcn());
        }

        Class<?> clazz = defineClass(generatedClass.fqcn(), generatedClass.bytes(), 0, generatedClass.bytes().length, null);

        this.generatedClassMap.put(generatedClass.fqcn(), generatedClass);
        this.classLookup.put(generatedClass.fqcn(), clazz);
        return clazz;
    }

    public Map<String, GeneratedClass> getGeneratedClassMap() {
        return generatedClassMap;
    }

    public Map<String, Class<?>> getClassLookup() {
        return classLookup;
    }
}
