package dev.toapuro.kubeextra.claasgen.generator;

import dev.toapuro.kubeextra.claasgen.KubeClass;
import javassist.CtClass;
import org.objectweb.asm.tree.ClassNode;

public record GeneratedClass(String fqcn, KubeClass kubeClass, byte[] bytes, CtClass ctClass, ClassNode classNode) {
}
