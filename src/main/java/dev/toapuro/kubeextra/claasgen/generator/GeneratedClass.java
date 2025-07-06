package dev.toapuro.kubeextra.claasgen.generator;

import dev.toapuro.kubeextra.claasgen.KubeClass;
import javassist.CtClass;

public record GeneratedClass(String fqcn, KubeClass kubeClass, byte[] bytes, CtClass ctClass) {
}
