package dev.toapuro.kubeextra.claasgen;

import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;

import java.util.ArrayList;
import java.util.List;

public class KubeClass {
    private final String className;
    private final List<KubeMethod> methods;
    private final List<KubeAnnotation> annotations;
    private boolean hasEmptyConstructor;

    public KubeClass(String className) {
        this.className = className;
        this.hasEmptyConstructor = false;
        this.methods = new ArrayList<>();
        this.annotations = new ArrayList<>();
    }

    public void addAnnotation(KubeAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public void addMethod(KubeMethod kubeMethod) {
        this.methods.add(kubeMethod);
    }

    public String getClassName() {
        return className;
    }

    public List<KubeMethod> getMethods() {
        return methods;
    }

    public List<KubeAnnotation> getAnnotations() {
        return annotations;
    }

    public boolean hasEmptyConstructor() {
        return hasEmptyConstructor;
    }

    public void setHasEmptyConstructor(boolean emptyConstructor) {
        this.hasEmptyConstructor = emptyConstructor;
    }
}
