package dev.toapuro.advancedkjs.claasgen.generator;

import dev.toapuro.advancedkjs.claasgen.gens.GenMethod;
import javassist.CtConstructor;

public record GeneratedConstructor(GenMethod genMethod, CtConstructor ctConstructor) {
}
