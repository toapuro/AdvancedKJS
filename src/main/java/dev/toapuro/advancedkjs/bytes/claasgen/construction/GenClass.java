package dev.toapuro.advancedkjs.bytes.claasgen.construction;

import dev.latvian.mods.rhino.BaseFunction;
import dev.toapuro.advancedkjs.bytes.claasgen.kubejs.callback.InstantFunction;
import javassist.CtClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GenClass {
    private final String fqcn;
    private final String className;
    private final List<InstantFunction> instants;
    private final List<GenField> fields;
    private final List<GenMethod> methods;
    private final List<GenConstructor> constructors;
    private final List<GenAnnotation> annotations;
    private final List<CtClass> implementsClasses;
    @Nullable
    private CtClass superClass;
    private int modifiers;

    public GenClass(String fqcn, String className) {
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

    public InstantFunction pushInstantFunction(BaseFunction instant) {
        InstantFunction instantFunction = new InstantFunction(className, instant.size(), instant);
        this.instants.add(instantFunction);
        return instantFunction;
    }

    public void addAnnotation(GenAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public void addField(GenField genField) {
        this.fields.add(genField);
    }

    public void addConstructor(GenConstructor kubeConstructor) {
        this.constructors.add(kubeConstructor);
    }

    public void addImplementsClass(CtClass implementsClass) {
        this.implementsClasses.add(implementsClass);
    }

    public void addMethod(GenMethod genMethod) {
        this.methods.add(genMethod);
    }

    public String getFqcn() {
        return fqcn;
    }

    public List<InstantFunction> getInstants() {
        return instants;
    }

    public List<GenField> getFields() {
        return fields;
    }

    public String getClassName() {
        return className;
    }

    public List<GenConstructor> getConstructors() {
        return constructors;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public List<GenMethod> getMethods() {
        return methods;
    }

    public List<GenAnnotation> getAnnotations() {
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
