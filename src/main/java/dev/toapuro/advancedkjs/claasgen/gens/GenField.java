package dev.toapuro.advancedkjs.claasgen.gens;

import dev.toapuro.advancedkjs.claasgen.annotation.KubeAnnotation;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;

public class GenField {
    private final CtClass fieldType;
    private final String fieldName;
    private final List<KubeAnnotation> annotations;
    private final int modifiers;

    public GenField(CtClass fieldType, String fieldName, int modifiers) {
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.annotations = new ArrayList<>();
        this.modifiers = modifiers;
    }

    public void addAnnotation(KubeAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public CtClass getFieldType() {
        return fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public List<KubeAnnotation> getAnnotations() {
        return annotations;
    }

    public int getModifiers() {
        return modifiers;
    }
}
