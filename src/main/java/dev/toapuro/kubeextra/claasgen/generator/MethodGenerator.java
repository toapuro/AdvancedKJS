package dev.toapuro.kubeextra.claasgen.generator;

import dev.toapuro.kubeextra.claasgen.KubeMethod;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.kubeextra.claasgen.kubejs.JavaMethodContext;
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
    protected Map<KubeMethod, GeneratedMethod> methodCache;

    public MethodGenerator() {
        this.methodCache = new HashMap<>();
    }

    public void clearCache() {
        this.methodCache.clear();
    }

    private static BytecodeLocalStack addLoadBoxedValueFromArgument(Bytecode bytecode, CtClass type, int index) {
        BytecodeLocalStack localStack = new BytecodeLocalStack();
        localStack.pushStack();

        if (type == CtClass.intType) {
            bytecode.addIload(index);
            bytecode.addInvokestatic("java.lang.Integer", "valueOf", "(I)Ljava/lang/Integer;");
        } else if (type == CtClass.longType) {
            bytecode.addLload(index);
            bytecode.addInvokestatic("java.lang.Long", "valueOf", "(J)Ljava/lang/Long;");
        } else if (type == CtClass.floatType) {
            bytecode.addFload(index);
            bytecode.addInvokestatic("java.lang.Float", "valueOf", "(F)Ljava/lang/Float;");
        } else if (type == CtClass.doubleType) {
            bytecode.addDload(index);
            bytecode.addInvokestatic("java.lang.Double", "valueOf", "(D)Ljava/lang/Double;");
        } else if (type == CtClass.booleanType) {
            bytecode.addIload(index);
            bytecode.addInvokestatic("java.lang.Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        } else if (type == CtClass.charType) {
            bytecode.addIload(index);
            bytecode.addInvokestatic("java.lang.Character", "valueOf", "(C)Ljava/lang/Character;");
        } else if (type == CtClass.byteType) {
            bytecode.addIload(index);
            bytecode.addInvokestatic("java.lang.Byte", "valueOf", "(B)Ljava/lang/Byte;");
        } else if (type == CtClass.shortType) {
            bytecode.addIload(index);
            bytecode.addInvokestatic("java.lang.Short", "valueOf", "(S)Ljava/lang/Short;");
        } else if (!type.isPrimitive()) {
            bytecode.addAload(index);
        } else {
            throw new RuntimeException("Could not find type " + type);
        }

        return localStack;
    }

    public GeneratedMethod generateMethod(KubeMethod kubeMethod, JavaMethodContext context) {
        if (methodCache.containsKey(kubeMethod)) {
            return methodCache.get(kubeMethod);
        }

        CtClass ctClass = context.getParentClass();

        CtClass returnClass = kubeMethod.getReturnType();

        MethodParameterTypes parameters = kubeMethod.getParameters();
        CtClass[] paramTypes = parameters.getCtArrayParams();

        CtMethod ctMethod = new CtMethod(
                returnClass,
                kubeMethod.getMethodName(),
                paramTypes,
                ctClass
        );

        for (KubeAnnotation annotation : kubeMethod.getAnnotations()) {
            context.addAnnotation(annotation);
        }
        context.buildAnnotations(ctMethod);

        ctMethod.setModifiers(kubeMethod.getModifiers());

        writeMethod(kubeMethod, ctMethod, context);

        GeneratedMethod generatedMethod = new GeneratedMethod(kubeMethod, ctMethod);
        methodCache.put(kubeMethod, generatedMethod);

        return generatedMethod;
    }

    public BytecodeLocalStack addMethodCallArguments(KubeMethod kubeMethod, Bytecode bytecode, int localOffset) {
        CtClass[] paramTypes = kubeMethod.getParameters().getCtArrayParams();

        BytecodeLocalStack localStack = new BytecodeLocalStack();

        // new Object[paramTypes.length]
        // obj[0] = arg0
        bytecode.addIconst(paramTypes.length);
        localStack.pushStack();
        bytecode.addAnewarray("java.lang.Object");

        for (int i = 0; i < paramTypes.length; i++) {
            bytecode.add(Bytecode.DUP);
            bytecode.addIconst(i);
            localStack.pushStack(2);

            // pushStack()
            localStack.apply(addLoadBoxedValueFromArgument(bytecode, paramTypes[i], localStack.getLocal() + localOffset));

            bytecode.add(Bytecode.AASTORE);
            localStack.popStack(3);
            localStack.pushLocal(isWideType(paramTypes[i]) ? 2 : 1);
        }

        return localStack;
    }

    public BytecodeLocalStack addHandleMethodCall(KubeMethod kubeMethod, Bytecode bytecode, JavaMethodContext context) {
        /*
        Code:
         KubeJSImplHandler.handleMethodCall(this, className, methodNameDesc, new Object[]{ arg1, arg2 });
        */

        BytecodeLocalStack localStack = new BytecodeLocalStack();

        int localOffset = 0;

        // load this
        if (Modifier.isStatic(kubeMethod.getModifiers())) {
            bytecode.add(Opcode.ACONST_NULL);
        } else {
            localOffset++;
            bytecode.addAload(0);
        }
        localStack.pushStack();

        // load className
        bytecode.addLdc(context.getParentClass().getName());
        localStack.pushStack();

        // load name + desc
        bytecode.addLdc(kubeMethod.getMethodName() + kubeMethod.getDescriptor());
        localStack.pushStack();

        localStack.apply(addMethodCallArguments(kubeMethod, bytecode, localOffset));

        bytecode.addInvokestatic("dev/toapuro/kubeextra/claasgen/kubejs/KubeJSImplHandler", "handleMethodCall",
                "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
        localStack.popStack(4);
        localStack.pushStack();

        return localStack;
    }

    public void writeMethod(KubeMethod kubeMethod, CtMethod method, JavaMethodContext context) {
        ConstPool constPool = context.getConstPool();
        MethodInfo methodInfo = method.getMethodInfo();

        Bytecode bytecode = new Bytecode(constPool);

        BytecodeLocalStack localStack = new BytecodeLocalStack();

        localStack.apply(addHandleMethodCall(kubeMethod, bytecode, context));

        CtClass returnType = kubeMethod.getReturnType();
        if (returnType == CtClass.voidType) {
            bytecode.add(Opcode.POP);
            localStack.popStack();

            bytecode.add(Bytecode.RETURN);
        } else {
            localStack.apply(addReturnUnboxed(bytecode, returnType));
        }

        CodeAttribute codeAttr = bytecode.toCodeAttribute();
        codeAttr.setMaxLocals(localStack.getMaxLocal() + 3);
        codeAttr.setMaxStack(localStack.getMaxStack() + 3); // Hardcoded
        methodInfo.setCodeAttribute(codeAttr);
    }

    public BytecodeLocalStack addReturnUnboxed(Bytecode bytecode, CtClass returnValue) {
        BytecodeLocalStack localStack = new BytecodeLocalStack();
        localStack.popStack();

        if (returnValue == CtClass.intType) {
            bytecode.add(Bytecode.IRETURN);
        } else if (returnValue == CtClass.longType) {
            bytecode.add(Bytecode.LRETURN);
        } else if (returnValue == CtClass.floatType) {
            bytecode.add(Bytecode.FRETURN);
        } else if (returnValue == CtClass.doubleType) {
            bytecode.add(Bytecode.DRETURN);
        } else if (returnValue == CtClass.booleanType) {
            bytecode.add(Bytecode.IRETURN);
        } else if (returnValue == CtClass.charType) {
            bytecode.add(Bytecode.IRETURN);
        } else if (returnValue == CtClass.byteType) {
            bytecode.add(Bytecode.IRETURN);
        } else if (returnValue == CtClass.shortType) {
            bytecode.add(Bytecode.IRETURN);
        } else if (!returnValue.isPrimitive()) {
            bytecode.add(Bytecode.ARETURN);
        } else {
            throw new RuntimeException("Could not find type " + returnValue);
        }

        return localStack;
    }

    private static boolean isWideType(CtClass type) {
        return type == CtClass.longType || type == CtClass.doubleType;
    }
}