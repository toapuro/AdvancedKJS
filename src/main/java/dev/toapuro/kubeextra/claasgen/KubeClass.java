package dev.toapuro.kubeextra.claasgen;

import dev.latvian.mods.rhino.BaseFunction;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.kubejs.KubeConstructor;
import dev.toapuro.kubeextra.claasgen.kubejs.KubeField;
import dev.toapuro.kubeextra.claasgen.kubejs.callback.InstantFunction;
import javassist.CtClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class KubeClass {
    private final String fqcn;
    private final String className;
    private final List<InstantFunction> instants;
    private final List<KubeField> fields;
    private final List<KubeMethod> methods;
    private final List<KubeConstructor> constructors;
    private final List<KubeAnnotation> annotations;
    private final List<CtClass> implementsClasses;
    @Nullable
    private CtClass superClass;
    private int modifiers;

    public KubeClass(String fqcn, String className) {
        this.fqcn = fqcn;
        this.className = className;
        this.superClass = null;

        this.modifiers = 0;
        this.instants = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.annotations = new ArrayList<>();
        this.constructors = new ArrayList<>();
        this.implementsClasses = new ArrayList<>();
    }

    public InstantFunction addInstantFunction(BaseFunction instant) {
        InstantFunction instantFunction = new InstantFunction(className, instant.size(), instant);
        this.instants.add(instantFunction);
        return instantFunction;
    }

    public void addAnnotation(KubeAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public void addField(KubeField kubeField) {
        this.fields.add(kubeField);
    }

    public void addConstructor(KubeConstructor kubeConstructor) {
        this.constructors.add(kubeConstructor);
    }

    public void addImplementsClass(CtClass implementsClass) {
        this.implementsClasses.add(implementsClass);
    }

    public void addMethod(KubeMethod kubeMethod) {
        this.methods.add(kubeMethod);
    }

    public String getFqcn() {
        return fqcn;
    }

    public List<InstantFunction> getInstants() {
        return instants;
    }

    public List<KubeField> getFields() {
        return fields;
    }

    public String getClassName() {
        return className;
    }

    public List<KubeConstructor> getConstructors() {
        return constructors;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public List<KubeMethod> getMethods() {
        return methods;
    }

    public List<KubeAnnotation> getAnnotations() {
        return annotations;
    }

    public boolean hasSuperClass() {
        return superClass != null;
    }

    @Nullable
    public CtClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(@Nullable CtClass superClass) {
        this.superClass = superClass;
    }

    public List<CtClass> getImplementsClasses() {
        return implementsClasses;
    }
}
