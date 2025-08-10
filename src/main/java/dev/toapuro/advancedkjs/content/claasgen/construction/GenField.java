package dev.toapuro.advancedkjs.content.claasgen.construction;

import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;

public class GenField {
    private final CtClass fieldType;
    private final String fieldName;
    private final List<GenAnnotation> annotations;
    private final int modifiers;

    public GenField(CtClass fieldType, String fieldName, int modifiers) {
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.annotations = new ArrayList<>();
        this.modifiers = modifiers;
    }

    public void addAnnotation(GenAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public CtClass getFieldType() {
        return fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public List<GenAnnotation> getAnnotations() {
        return annotations;
    }

    public int getModifiers() {
        return modifiers;
    }
}
