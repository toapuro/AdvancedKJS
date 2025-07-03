package dev.toapuro.kubeextra.claasgen.kubejs;

import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

import java.util.ArrayList;
import java.util.List;

public class JavaClassContext {
    private final CtClass ctClass;
    private final ClassFile classFile;
    private final ConstPool constPool;
    private final List<KubeAnnotation> annotations;

    public JavaClassContext(ConstPool constPool, ClassFile classFile, CtClass ctClass) {
        this.constPool = constPool;
        this.classFile = classFile;
        this.ctClass = ctClass;
        this.annotations = new ArrayList<>();
    }

    public void addAnnotation(KubeAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public void buildAnnotations() {
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        for (KubeAnnotation annotation : this.annotations) {
            attribute.addAnnotation(annotation.buildAnnotation(constPool));
        }
        classFile.addAttribute(attribute);
    }

    public CtClass getCtClass() {
        return ctClass;
    }

    public ClassFile getClassFile() {
        return classFile;
    }

    public ConstPool getConstPool() {
        return constPool;
    }
}
