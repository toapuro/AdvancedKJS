package dev.toapuro.kubeextra.claasgen.generator;

import dev.toapuro.kubeextra.claasgen.KubeClass;
import dev.toapuro.kubeextra.claasgen.KubeMethod;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.kubejs.JavaClassContext;
import dev.toapuro.kubeextra.claasgen.kubejs.JavaMethodContext;
import dev.toapuro.kubeextra.claasgen.kubejs.KubeConstructor;
import dev.toapuro.kubeextra.claasgen.kubejs.KubeField;
import dev.toapuro.kubeextra.handler.CtClassLookupHandler;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassGenerator.class);
    private final Map<KubeClass, GeneratedClass> classCache;
    private final MethodGenerator methodGenerator;
    private final ConstructorGenerator constGenerator;

    public ClassGenerator() {
        this.classCache = new HashMap<>();
        this.methodGenerator = new MethodGenerator();
        this.constGenerator = new ConstructorGenerator();
    }

    public void clearCache() {
        this.classCache.clear();
        this.methodGenerator.clearCache();
        this.constGenerator.clearCache();
    }

    private void addClassAnnotations(KubeClass kubeClass, JavaClassContext context) {
        kubeClass.getAnnotations().forEach(context::addAnnotation);
    }

    public static ClassNode writeClassNode(CtClass ctClass) throws Exception {
        byte[] bytecode = ctClass.toBytecode();
        ClassReader reader = new ClassReader(bytecode);

        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.EXPAND_FRAMES);
        return node;
    }

    public CtField generateField(CtClass ctClass, ConstPool constPool, KubeField field) throws CannotCompileException {
        CtField ctField = new CtField(field.getFieldType(), field.getFieldName(), ctClass);
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        for (KubeAnnotation annotation : field.getAnnotations()) {
            attribute.addAnnotation(annotation.compileAnnotation(constPool));
        }

        ctField.getFieldInfo().addAttribute(attribute);
        ctField.setModifiers(field.getModifiers());

        return ctField;
    }

    public GeneratedClass generateClass(KubeClass kubeClass) {
        if(classCache.containsKey(kubeClass)) {
            return classCache.get(kubeClass);
        }

        // Generate class
        ClassPool pool = ClassPool.getDefault();

        CtClassLookupHandler.lookup(kubeClass.getFqcn()).ifPresent(CtClass::defrost);

        CtClass ctClass = pool.makeClass(kubeClass.getFqcn());

        if (!kubeClass.getConstructors().isEmpty()) {
            for (CtConstructor c : ctClass.getDeclaredConstructors()) {
                try {
                    ctClass.removeConstructor(c);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        JavaClassContext context = new JavaClassContext(constPool, classFile ,ctClass);

        ctClass.setModifiers(kubeClass.getModifiers());

        CtClass superClass = kubeClass.getSuperClass();
        if (superClass != null) {
            try {
                ctClass.setSuperclass(superClass);
            } catch (CannotCompileException e) {
                LOGGER.error("Could not set superclass {} in {}", superClass.getName(), kubeClass.getClassName(), e);
                throw new RuntimeException(e);
            }
        }

        List<CtClass> implementsClasses = kubeClass.getImplementsClasses();
        if (!implementsClasses.isEmpty()) {
            ctClass.setInterfaces(implementsClasses.toArray(CtClass[]::new));
        }

        addClassAnnotations(kubeClass, context);
        context.buildAnnotations();

        // Generate methods
        try {
            for (KubeConstructor kubeConstructor : kubeClass.getConstructors()) {
                JavaMethodContext methodContext = new JavaMethodContext(context, ctClass, constPool, pool);
                GeneratedConstructor generated = constGenerator.generateConstructor(kubeConstructor, methodContext);
                ctClass.addConstructor(generated.ctConstructor());
            }

            for (KubeMethod kubeMethod : kubeClass.getMethods()) {
                JavaMethodContext methodContext = new JavaMethodContext(context, ctClass, constPool, pool);
                GeneratedMethod generated = methodGenerator.generateMethod(kubeMethod, methodContext);
                ctClass.addMethod(generated.ctMethod());
            }
        } catch (Exception e) {
            LOGGER.error("Could not add methods in class {}", kubeClass.getClassName(), e);
            throw new RuntimeException(e);
        }

        try {
            for (KubeField kubeField : kubeClass.getFields()) {
                CtField ctField = generateField(ctClass, constPool, kubeField);
                ctClass.addField(ctField);
            }
        } catch (Exception e) {
            LOGGER.error("Could not add fields in class {}", kubeClass.getClassName(), e);
            throw new RuntimeException(e);
        }

        classFile.compact();

        try {
            ctClass.writeFile("out");

            GeneratedClass generatedClass = new GeneratedClass(kubeClass.getFqcn(), kubeClass, ctClass.toBytecode(), ctClass);
            classCache.put(kubeClass, generatedClass);

            return generatedClass;
        } catch (Exception e) {
            LOGGER.error("Could not generate class {}", kubeClass.getClassName(), e);
            throw new RuntimeException(e);
        }
    }
}
