package dev.toapuro.advancedkjs.bytes.claasgen.handler;

import dev.toapuro.advancedkjs.bytes.claasgen.generated.GeneratedClass;

import java.util.HashMap;
import java.util.Map;

public class AdvancedKJSClassLoader extends ClassLoader {
    private final Map<String, Class<?>> genClassLookup;

    public AdvancedKJSClassLoader(ClassLoader parent) {
        super(parent);
        this.genClassLookup = new HashMap<>();
    }

    public final Class<?> defineOrGet(GeneratedClass generatedClass) throws ClassFormatError
    {
        if (genClassLookup.containsKey(generatedClass.fqcn())) {
            return genClassLookup.get(generatedClass.fqcn());
        }

        Class<?> clazz = defineClass(generatedClass.fqcn(), generatedClass.bytes(), 0, generatedClass.bytes().length, null);

        this.genClassLookup.put(generatedClass.fqcn(), clazz);
        return clazz;
    }

    public Map<String, Class<?>> getGenClassLookup() {
        return genClassLookup;
    }
}
