package dev.toapuro.advancedkjs.claasgen.generator;

import dev.toapuro.advancedkjs.claasgen.gens.GenMethod;
import javassist.CtMethod;

public record GeneratedMethod(GenMethod genMethod, CtMethod ctMethod) {
}
