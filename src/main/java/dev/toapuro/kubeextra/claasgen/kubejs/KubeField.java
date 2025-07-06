package dev.toapuro.kubeextra.claasgen.kubejs;

import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;

public class KubeField {
    private final CtClass fieldType;
    private final String fieldName;
    private final Object initialValue;
    private final List<KubeAnnotation> annotations;
    private final int modifiers;

    public KubeField(CtClass fieldType, String fieldName, Object initialValue, int modifiers) {
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.initialValue = initialValue;
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

    public Object getInitialValue() {
        return initialValue;
    }

    public List<KubeAnnotation> getAnnotations() {
        return annotations;
    }

    public int getModifiers() {
        return modifiers;
    }
}
