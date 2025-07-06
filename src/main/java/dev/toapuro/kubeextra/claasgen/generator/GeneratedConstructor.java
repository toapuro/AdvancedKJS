package dev.toapuro.kubeextra.claasgen.generator;

import dev.toapuro.kubeextra.claasgen.KubeMethod;
import javassist.CtConstructor;

public record GeneratedConstructor(KubeMethod kubeMethod, CtConstructor ctConstructor) {
}
