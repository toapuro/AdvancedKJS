package dev.toapuro.kubeextra.claasgen.kubejs;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;

public class JavaMethodContext {
    private final JavaClassContext classContext;
    private final CtClass parentClass;
    private final ConstPool constPool;
    private final ClassPool classPool;
    private final List<Annotation> annotations;

    public JavaMethodContext(JavaClassContext classContext, CtClass parentClass, ConstPool constPool, ClassPool classPool) {
        this.classContext = classContext;
        this.parentClass = parentClass;
        this.constPool = constPool;
        this.classPool = classPool;
        this.annotations = new ArrayList<>();
    }

    public void addAnnotation(Annotation annotation) {
        this.annotations.add(annotation);
    }

    public void buildAnnotations(CtMethod ctMethod) {
    }

    public JavaClassContext getClassContext() {
        return classContext;
    }

    public CtClass getParentClass() {
        return parentClass;
    }

    public ConstPool getConstPool() {
        return constPool;
    }

    public ClassPool getClassPool() {
        return classPool;
    }
}
