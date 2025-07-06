package dev.toapuro.kubeextra.claasgen.generator;

import dev.toapuro.kubeextra.claasgen.KubeClass;
import dev.toapuro.kubeextra.claasgen.KubeMethod;
import dev.toapuro.kubeextra.claasgen.annotation.KubeAnnotation;
import dev.toapuro.kubeextra.claasgen.arguments.MethodParameterTypes;
import dev.toapuro.kubeextra.claasgen.arguments.SuperParamArgument;
import dev.toapuro.kubeextra.claasgen.arguments.SuperParamConstArgument;
import dev.toapuro.kubeextra.claasgen.arguments.SuperParamFunctionArgument;
import dev.toapuro.kubeextra.claasgen.kubejs.JavaMethodContext;
import dev.toapuro.kubeextra.claasgen.kubejs.KubeConstructor;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.bytecode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConstructorGenerator extends MethodGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConstructorGenerator.class);
    protected Map<KubeMethod, GeneratedConstructor> constructorCache;

    public ConstructorGenerator() {
        this.constructorCache = new HashMap<>();
    }

    @Override
    public void clearCache() {
        constructorCache.clear();
    }

    public GeneratedConstructor generateConstructor(KubeConstructor kubeConstructor, JavaMethodContext context) {
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

        for (KubeAnnotation annotation : kubeConstructor.getAnnotations()) {
            context.addAnnotation(annotation);
        }
        context.buildAnnotations(ctConstructor);

        ctConstructor.setModifiers(kubeConstructor.getModifiers());

        writeConstructor(kubeConstructor, kubeConstructor.getParentClass().hasSuperClass(), ctConstructor, context);

        GeneratedConstructor generatedMethod = new GeneratedConstructor(kubeConstructor, ctConstructor);
        constructorCache.put(kubeConstructor, generatedMethod);

        return generatedMethod;
    }

    public void writeConstructor(KubeConstructor kubeConstructor, boolean shouldCallSuper, CtConstructor method, JavaMethodContext context) {
        ConstPool constPool = context.getConstPool();
        MethodInfo methodInfo = method.getMethodInfo();

        Bytecode bytecode = new Bytecode(constPool);

        BytecodeLocalStack localStack = new BytecodeLocalStack();

        if (shouldCallSuper) {
            bytecode.addAload(0);
            LOGGER.info("ALOAD 0");

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
                    /*
                    Code:
                     KubeJSImplHandler.handleInstantCall(this, className, instantId, new Object[]{ arg1, arg2 });
                    */


                    int localOffset = 0;

                    localOffset++;
                    bytecode.add(Opcode.ACONST_NULL);
                    localStack.pushStack();
                    LOGGER.info("ACONST NULL");

                    // load className
                    bytecode.addLdc(context.getParentClass().getName());
                    localStack.pushStack();
                    LOGGER.info("LDC {}", context.getParentClass().getName());

                    // load instantId
                    bytecode.addIconst(functionArgument.getInstant().instantId());
                    localStack.pushStack();
                    LOGGER.info("LDC {}", functionArgument.getInstant().instantId());

                    localStack.apply(addMethodCallArguments(kubeConstructor, bytecode, localOffset));
                    LOGGER.info("addMethodCallArguments {}", kubeConstructor.getParameters().getCtArrayParams().length);

                    bytecode.addInvokestatic("dev/toapuro/kubeextra/claasgen/kubejs/KubeJSImplHandler", "handleInstantCall",
                            "(Ljava/lang/Object;Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/Object;");
                    localStack.popStack(4);
                    localStack.pushStack();
                    LOGGER.info("INVOKESTATIC KubeJSImplHandler.handleInstantCall({}, {}, {}, {})", "null", context.getParentClass().getName(), functionArgument.getInstant().instantId(), "{}");

                    bytecode.addCheckcast(superArgument.getParamClass());
                    LOGGER.info("CHECKCAST Object -> {}", superArgument.getParamClass().getName());
                }
            }

            // call constructor
            KubeClass parentClass = kubeConstructor.getParentClass();
            CtClass[] arguments = kubeConstructor.getSuperArguments().stream().map(SuperParamArgument::getParamClass).toArray(CtClass[]::new);
            String superDescriptor = Descriptor.ofConstructor(arguments);


            bytecode.addInvokespecial(parentClass.getSuperClass(), "<init>", superDescriptor);
            LOGGER.info("{} -> super {}{}", parentClass.getSuperClass().getName(), "<init>", superDescriptor);
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