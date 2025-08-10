package dev.toapuro.advancedkjs.content.claasgen.generator;

import dev.toapuro.advancedkjs.content.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.advancedkjs.content.claasgen.arguments.SuperParamArgument;
import dev.toapuro.advancedkjs.content.claasgen.arguments.SuperParamConstArgument;
import dev.toapuro.advancedkjs.content.claasgen.arguments.SuperParamFunctionArgument;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenAnnotation;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenClass;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenConstructor;
import dev.toapuro.advancedkjs.content.claasgen.construction.GenMethod;
import dev.toapuro.advancedkjs.content.claasgen.generated.GeneratedConstructor;
import dev.toapuro.advancedkjs.content.claasgen.kubejs.JavaMethodContext;
import dev.toapuro.advancedkjs.content.utils.BytecodeLocalStack;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.bytecode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConstructorGenerator extends MethodGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConstructorGenerator.class);
    protected Map<GenMethod, GeneratedConstructor> constructorCache;

    public ConstructorGenerator() {
        this.constructorCache = new HashMap<>();
    }

    @Override
    public void clearCache() {
        constructorCache.clear();
    }

    public GeneratedConstructor generateConstructor(GenConstructor kubeConstructor, JavaMethodContext context) {
        if (constructorCache.containsKey(kubeConstructor)) {
            return constructorCache.get(kubeConstructor);
        }

        CtClass ctClass = context.getParentClass();

        MethodParameterTypes parameters = kubeConstructor.getParameters();
        CtClass[] paramTypes = parameters.getCtArrayParams();

        CtConstructor ctConstructor = new CtConstructor(
                paramTypes,
                ctClass
        );

        for (GenAnnotation annotation : kubeConstructor.getAnnotations()) {
            context.addAnnotation(annotation);
        }
        context.buildAnnotations(ctConstructor);

        ctConstructor.setModifiers(kubeConstructor.getModifiers());

        writeConstructor(kubeConstructor, kubeConstructor.getParentClass().hasSuperClass(), ctConstructor, context);

        GeneratedConstructor generatedMethod = new GeneratedConstructor(kubeConstructor, ctConstructor);
        constructorCache.put(kubeConstructor, generatedMethod);

        return generatedMethod;
    }

    public void writeConstructor(GenConstructor kubeConstructor, boolean shouldCallSuper, CtConstructor method, JavaMethodContext context) {
        ConstPool constPool = context.getConstPool();
        MethodInfo methodInfo = method.getMethodInfo();

        Bytecode bytecode = new Bytecode(constPool);

        BytecodeLocalStack localStack = new BytecodeLocalStack();

        if (shouldCallSuper) {
            bytecode.addAload(0);

            for (SuperParamArgument superArgument : kubeConstructor.getSuperArguments()) {
                if (superArgument instanceof SuperParamConstArgument constArgument) {
                    if (superArgument.getArgType() == SuperParamArgument.ArgType.CONST_I) {
                        bytecode.addIconst((Integer) constArgument.getInitialValue());
                    } else if (superArgument.getArgType() == SuperParamArgument.ArgType.CONST_D) {
                        bytecode.addDconst((Double) constArgument.getInitialValue());
                    } else if (superArgument.getArgType() == SuperParamArgument.ArgType.CONST_F) {
                        bytecode.addFconst((Float) constArgument.getInitialValue());
                    } else if (superArgument.getArgType() == SuperParamArgument.ArgType.CONST_L) {
                        bytecode.addLconst((Long) constArgument.getInitialValue());
                    }
                    localStack.pushStack();
                }

                if (superArgument instanceof SuperParamFunctionArgument functionArgument) {
                    localStack.apply(CommonGenerator.addInstantMethodCall(
                            kubeConstructor.getParentClass(), kubeConstructor.getParameters().getCtArrayParams(), bytecode, functionArgument.getInstant()));

                    bytecode.addCheckcast(superArgument.getParamClass());
                }
            }

            // call constructor
            GenClass parentClass = kubeConstructor.getParentClass();
            CtClass[] arguments = kubeConstructor.getSuperArguments().stream().map(SuperParamArgument::getParamClass).toArray(CtClass[]::new);
            String superDescriptor = Descriptor.ofConstructor(arguments);


            bytecode.addInvokespecial(parentClass.getSuperClass(), "<init>", superDescriptor);
        } else {
            // call object constructor
            bytecode.addAload(0);
            bytecode.addInvokespecial("java/lang/Object", "<init>", "()V");
        }

        localStack.apply(addHandleMethodCall(kubeConstructor, bytecode, context));

        bytecode.add(Opcode.POP);
        localStack.popStack();

        bytecode.add(Bytecode.RETURN);

        CodeAttribute codeAttr = bytecode.toCodeAttribute();
        codeAttr.setMaxLocals(localStack.getMaxLocal() + 3);
        codeAttr.setMaxStack(localStack.getMaxStack() + 3); // Hardcoded
        methodInfo.setCodeAttribute(codeAttr);
    }
}