package dev.toapuro.kubeextra.claasgen.generator;

import dev.toapuro.kubeextra.claasgen.KubeMethod;
import javassist.CtMethod;

public record GeneratedMethod(KubeMethod kubeMethod, CtMethod ctMethod) {
}
