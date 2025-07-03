package dev.toapuro.kubeextra.claasgen.generator;

import dev.toapuro.kubeextra.claasgen.KubeMethod;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.kubejs.JavaMethodContext;
import dev.toapuro.kubeextra.claasgen.parameter.MethodParameterTypes;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MethodGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodGenerator.class);
    private Map<KubeMethod, GeneratedMethod> methodCache;

    public MethodGenerator() {
        this.methodCache = new HashMap<>();
    }

    public void clearCache() {
        this.methodCache.clear();
    }

    public GeneratedMethod generateMethod(KubeMethod kubeMethod, JavaMethodContext context) {
        if (methodCache.containsKey(kubeMethod)) {
            return methodCache.get(kubeMethod);
        }

        CtClass ctClass = context.getParentClass();

        CtClass returnClass = kubeMethod.returnType();

        MethodParameterTypes parameters = kubeMethod.parameters();
        CtClass[] paramTypes = parameters.getCtArrayParams();

        CtMethod method = new CtMethod(
                returnClass,
                kubeMethod.methodName(),
                paramTypes,
                ctClass
        );

        for (KubeAnnotation annotation : kubeMethod.annotations()) {
            context.addAnnotation(annotation.buildAnnotation(ctClass.getClassFile().getConstPool()));
        }
        context.buildAnnotations(method);

        method.setModifiers(kubeMethod.modifiers());

        writeMethod(kubeMethod, method, paramTypes, context);

        GeneratedMethod generatedMethod = new GeneratedMethod(kubeMethod, method);
        methodCache.put(kubeMethod, generatedMethod);

        return generatedMethod;
    }

    public void writeMethod(KubeMethod kubeMethod, CtMethod method, CtClass[] paramTypes, JavaMethodContext context) {
        ConstPool constPool = context.getConstPool();
        MethodInfo methodInfo = method.getMethodInfo();

        Bytecode bytecode = new Bytecode(constPool);

        /*
        Code:
         KubeJSImplHandler.handleMethodCall(this, className, methodName, new Object[]{ arg1, arg2 });
        */

        // load this
        if(!Modifier.isStatic(kubeMethod.modifiers())) {
            bytecode.addAload(0);
        } else {
            bytecode.add(Opcode.ACONST_NULL);
        }

        // load className
        bytecode.addLdc(context.getParentClass().getName());

        // load methodName
        bytecode.addLdc(method.getName());

        // new Object[paramTypes.length]{}
        bytecode.addIconst(paramTypes.length);
        bytecode.addAnewarray("java.lang.Object");

        int localIndex = 1;

        for (int i = 0; i < paramTypes.length; i++) {
            bytecode.add(Bytecode.DUP);
            bytecode.addIconst(i);

            loadBoxedValueFromArgument(bytecode, paramTypes[i], localIndex);

            bytecode.add(Bytecode.AASTORE);
            localIndex += isWideType(paramTypes[i]) ? 2 : 1;
        }

        bytecode.addInvokestatic("dev.toapuro.kubeextra.claasgen.kubejs.KubeJSImplHandler", "handleMethodCall",
                "(Ljava/lang/Object;Ljava.lang.String;Ljava.lang.String;[Ljava/lang/Object;)V");

        bytecode.add(Bytecode.RETURN);

        CodeAttribute codeAttr = bytecode.toCodeAttribute();
        codeAttr.setMaxLocals(localIndex);
        codeAttr.setMaxStack(5); // Hardcoded
        methodInfo.setCodeAttribute(codeAttr);
    }

    private static void loadBoxedValueFromArgument(Bytecode bytecode, CtClass type, int localIndex) {
        if (type == CtClass.intType) {
            bytecode.addIload(localIndex);
            bytecode.addInvokestatic("java.lang.Integer", "valueOf", "(I)Ljava/lang/Integer;");
        } else if (type == CtClass.longType) {
            bytecode.addLload(localIndex);
            bytecode.addInvokestatic("java.lang.Long", "valueOf", "(J)Ljava/lang/Long;");
        } else if (type == CtClass.floatType) {
            bytecode.addFload(localIndex);
            bytecode.addInvokestatic("java.lang.Float", "valueOf", "(F)Ljava/lang/Float;");
        } else if (type == CtClass.doubleType) {
            bytecode.addDload(localIndex);
            bytecode.addInvokestatic("java.lang.Double", "valueOf", "(D)Ljava/lang/Double;");
        } else if (type == CtClass.booleanType) {
            bytecode.addIload(localIndex);
            bytecode.addInvokestatic("java.lang.Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        } else if (type == CtClass.charType) {
            bytecode.addIload(localIndex);
            bytecode.addInvokestatic("java.lang.Character", "valueOf", "(C)Ljava/lang/Character;");
        } else if (type == CtClass.byteType) {
            bytecode.addIload(localIndex);
            bytecode.addInvokestatic("java.lang.Byte", "valueOf", "(B)Ljava/lang/Byte;");
        } else if (type == CtClass.shortType) {
            bytecode.addIload(localIndex);
            bytecode.addInvokestatic("java.lang.Short", "valueOf", "(S)Ljava/lang/Short;");
        } else if (!type.isPrimitive()) {
            bytecode.addAload(localIndex);
        } else {
            throw new RuntimeException("Could not find type " + type);
        }
    }

    private static boolean isWideType(CtClass type) {
        return type == CtClass.longType || type == CtClass.doubleType;
    }
}