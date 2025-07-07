package dev.toapuro.advancedkjs.claasgen.generator;

import dev.toapuro.advancedkjs.claasgen.gens.GenClass;
import javassist.CtClass;

public record GeneratedClass(String fqcn, GenClass genClass, byte[] bytes, CtClass ctClass) {
}
