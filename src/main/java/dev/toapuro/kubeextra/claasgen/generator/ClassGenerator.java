package dev.toapuro.kubeextra.claasgen.generator;

import dev.toapuro.kubeextra.claasgen.kubejs.JavaClassContext;
import dev.toapuro.kubeextra.claasgen.kubejs.JavaMethodContext;
import dev.toapuro.kubeextra.claasgen.HardcodedConfig;
import dev.toapuro.kubeextra.claasgen.gen.GeneratedClass;
import dev.toapuro.kubeextra.claasgen.gen.GeneratedMethod;
import dev.toapuro.kubeextra.claasgen.gen.KubeClass;
import dev.toapuro.kubeextra.claasgen.gen.KubeMethod;
import javassist.*;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ClassGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassGenerator.class);
    private final Map<KubeClass, GeneratedClass> classCache;
    private final KubeMethodGenerator generator;
    private int generatedCount = 0;

    public ClassGenerator() {
        this.classCache = new HashMap<>();
        this.generator = new KubeMethodGenerator();
    }

    public void clearCache() {
        this.classCache.clear();
        this.generator.clearCache();
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

    public GeneratedClass generateClass(KubeClass kubeClass) {
        if(classCache.containsKey(kubeClass)) {
            return classCache.get(kubeClass);
        }

        // Generate class
        ClassPool pool = ClassPool.getDefault();
        String fullClassName = HardcodedConfig.generatedPackage + "." + kubeClass.getClassName();
        CtClass ctClass = pool.makeClass(fullClassName);

        if(!kubeClass.hasEmptyConstructor()) {
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

        addClassAnnotations(kubeClass, context);
        context.buildAnnotations();

        // Generate methods
        try {
            for (KubeMethod kubeMethod : kubeClass.getMethods()) {
                JavaMethodContext methodContext = new JavaMethodContext(context, ctClass, constPool, pool);
                GeneratedMethod generated = generator.generateMethod(kubeMethod, methodContext);
                CtMethod ctMethod = generated.ctMethod();
                ctClass.addMethod(ctMethod);
            }
        } catch (Exception e) {
            LOGGER.error("Could not methods in class {}", kubeClass.getClassName(), e);
            throw new RuntimeException(e);
        }

        generatedCount++;

        classFile.compact();
        for (MethodInfo method : classFile.getMethods()) {
            try {
                method.rebuildStackMapIf6(new ClassPool(), classFile); // Java 6以上対応
            } catch (BadBytecode e) {
                throw new RuntimeException(e);
            }
        }

        try {
            ClassNode classNode = writeClassNode(ctClass);
            GeneratedClass generatedClass = new GeneratedClass(fullClassName, kubeClass, ctClass.toBytecode(), ctClass, classNode);
            classCache.put(kubeClass, generatedClass);

            ctClass.writeFile("out");

            return generatedClass;
        } catch (Exception e) {
            LOGGER.error("Could not generate class {}", kubeClass.getClassName(), e);
            throw new RuntimeException(e);
        }
    }
}
