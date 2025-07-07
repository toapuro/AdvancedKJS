package dev.toapuro.advancedkjs.claasgen.generator;

import dev.toapuro.advancedkjs.claasgen.gens.GenClass;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.InstantFunction;
import javassist.CtClass;
import javassist.bytecode.Bytecode;

public class CommonGenerator {
    public static BytecodeLocalStack addInstantMethodCall(GenClass genClass, CtClass[] parameters, Bytecode bytecode, InstantFunction instantFunction) {
        /*
        Code:
         KubeJSImplHandler.handleInstantCall(instance, className, methodNameDesc, new Object[]{ arg1, arg2 });
        */

        BytecodeLocalStack localStack = new BytecodeLocalStack();

        int localOffset = 0;

        // load this
        localOffset++;
        bytecode.addAload(0);
        localStack.pushStack();

        // load className
        bytecode.addLdc(genClass.getFqcn());
        localStack.pushStack();

        // load instantId
        bytecode.addIconst(instantFunction.instantId());
        localStack.pushStack();

        // load arguments
        localStack.apply(addMethodCallArguments(parameters, bytecode, localOffset));

        bytecode.addInvokestatic("dev/toapuro/advancedkjs/claasgen/kubejs/KubeJSImplHandler", "handleInstantCall",
                "(Ljava/lang/Object;Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/Object;");
        localStack.popStack(4);
        localStack.pushStack();

        return localStack;
    }

    public static BytecodeLocalStack addMethodCallArguments(CtClass[] paramTypes, Bytecode bytecode, int localOffset) {
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

    public static boolean isWideType(CtClass type) {
        return type == CtClass.longType || type == CtClass.doubleType;
    }

    public static BytecodeLocalStack addLoadBoxedValueFromArgument(Bytecode bytecode, CtClass type, int index) {
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
}
