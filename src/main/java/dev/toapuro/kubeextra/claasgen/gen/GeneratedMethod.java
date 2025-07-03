package dev.toapuro.kubeextra.claasgen.gen;

import javassist.CtMethod;

public record GeneratedMethod(KubeMethod kubeMethod, CtMethod ctMethod) {
}
