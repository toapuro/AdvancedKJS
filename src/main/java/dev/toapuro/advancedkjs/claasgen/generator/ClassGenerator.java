package dev.toapuro.advancedkjs.claasgen.generator;

import dev.toapuro.advancedkjs.claasgen.annotation.KubeAnnotation;
import dev.toapuro.advancedkjs.claasgen.gens.GenClass;
import dev.toapuro.advancedkjs.claasgen.gens.GenConstructor;
import dev.toapuro.advancedkjs.claasgen.gens.GenField;
import dev.toapuro.advancedkjs.claasgen.gens.GenMethod;
import dev.toapuro.advancedkjs.claasgen.kubejs.JavaClassContext;
import dev.toapuro.advancedkjs.claasgen.kubejs.JavaMethodContext;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.InstantFunction;
import dev.toapuro.advancedkjs.handler.CtClassLookupHandler;
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
    private final Map<GenClass, GeneratedClass> classCache;
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

    private void addClassAnnotations(GenClass genClass, JavaClassContext context) {
        genClass.getAnnotations().forEach(context::addAnnotation);
    }

    public static ClassNode writeClassNode(CtClass ctClass) throws Exception {
        byte[] bytecode = ctClass.toBytecode();
        ClassReader reader = new ClassReader(bytecode);

        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.EXPAND_FRAMES);
        return node;
    }

    public CtField generateField(CtClass ctClass, ConstPool constPool, GenField field) throws CannotCompileException {
        CtField ctField = new CtField(field.getFieldType(), field.getFieldName(), ctClass);
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        for (KubeAnnotation annotation : field.getAnnotations()) {
            attribute.addAnnotation(annotation.compileAnnotation(constPool));
        }

        ctField.getFieldInfo().addAttribute(attribute);
        ctField.setModifiers(field.getModifiers());

        return ctField;
    }

    public GeneratedClass generateClass(GenClass genClass) {
        if (classCache.containsKey(genClass)) {
            return classCache.get(genClass);
        }

        // Generate class
        ClassPool pool = ClassPool.getDefault();

        CtClassLookupHandler.lookup(genClass.getFqcn()).ifPresent(CtClass::defrost);

        CtClass ctClass = pool.makeClass(genClass.getFqcn());

        if (!genClass.getConstructors().isEmpty()) {
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

        ctClass.setModifiers(genClass.getModifiers());

        CtClass superClass = genClass.getSuperClass();
        if (superClass != null) {
            try {
                ctClass.setSuperclass(superClass);
            } catch (CannotCompileException e) {
                LOGGER.error("Could not set superclass {} in {}", superClass.getName(), genClass.getClassName(), e);
                throw new RuntimeException(e);
            }
        }

        List<CtClass> implementsClasses = genClass.getImplementsClasses();
        if (!implementsClasses.isEmpty()) {
            ctClass.setInterfaces(implementsClasses.toArray(CtClass[]::new));
        }

        addClassAnnotations(genClass, context);
        context.buildAnnotations();

        // Generate methods
        try {
            for (GenConstructor kubeConstructor : genClass.getConstructors()) {
                JavaMethodContext methodContext = new JavaMethodContext(context, ctClass, constPool, pool);
                GeneratedConstructor generated = constGenerator.generateConstructor(kubeConstructor, methodContext);
                ctClass.addConstructor(generated.ctConstructor());
            }

            for (GenMethod genMethod : genClass.getMethods()) {
                JavaMethodContext methodContext = new JavaMethodContext(context, ctClass, constPool, pool);
                GeneratedMethod generated = methodGenerator.generateMethod(genMethod, methodContext);
                ctClass.addMethod(generated.ctMethod());
            }
        } catch (Exception e) {
            LOGGER.error("Could not add methods in class {}", genClass.getClassName(), e);
            throw new RuntimeException(e);
        }

        try {
            for (GenField genField : genClass.getFields()) {
                CtField ctField = generateField(ctClass, constPool, genField);

                Object initialValue = genField.getInitialValue();
                if (initialValue instanceof InstantFunction instantFunction) {
                    CtClass kubeJSImplHandler = CtClassLookupHandler.lookupOrThrow("dev.toapuro.advancedkjs.claasgen.kubejs.KubeJSImplHandler");

                    ctClass.addField(ctField, CtField.Initializer.byCallWithParams(kubeJSImplHandler, "handleInstantCall", new String[]{
                            "this", genClass.getFqcn(), Integer.toString(instantFunction.instantId()), "new Object[0]"
                    }));
                } else {
                    ctClass.addField(ctField);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Could not add fields in class {}", genClass.getClassName(), e);
            throw new RuntimeException(e);
        }

        classFile.compact();

        try {
            ctClass.writeFile("out");

            GeneratedClass generatedClass = new GeneratedClass(genClass.getFqcn(), genClass, ctClass.toBytecode(), ctClass);
            classCache.put(genClass, generatedClass);

            return generatedClass;
        } catch (Exception e) {
            LOGGER.error("Could not generate class {}", genClass.getClassName(), e);
            throw new RuntimeException(e);
        }
    }
}
