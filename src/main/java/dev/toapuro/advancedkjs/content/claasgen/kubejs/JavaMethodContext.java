package dev.toapuro.advancedkjs.content.claasgen.kubejs;

import dev.toapuro.advancedkjs.content.claasgen.construction.GenAnnotation;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;

import java.util.ArrayList;
import java.util.List;

public class JavaMethodContext {
    private final JavaClassContext classContext;
    private final CtClass parentClass;
    private final ConstPool constPool;
    private final ClassPool classPool;
    private final List<GenAnnotation> annotations;

    public JavaMethodContext(JavaClassContext classContext, CtClass parentClass, ConstPool constPool, ClassPool classPool) {
        this.classContext = classContext;
        this.parentClass = parentClass;
        this.constPool = constPool;
        this.classPool = classPool;
        this.annotations = new ArrayList<>();
    }

    public void addAnnotation(GenAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public void buildAnnotations(CtBehavior behavior) {
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        for (GenAnnotation annotation : this.annotations) {
            attribute.addAnnotation(annotation.compileAnnotation(constPool));
        }
        behavior.getMethodInfo().addAttribute(attribute);
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
