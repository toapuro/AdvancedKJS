package dev.toapuro.advancedkjs.coremod;

import dev.toapuro.advancedkjs.util.CommonUtil;
import dev.toapuro.advancedkjs.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class AgentTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentTransformer.class);
    public static AgentTransformer INSTANCE = new AgentTransformer();
    private Instrumentation instrumentation;

    public AgentTransformer() {
    }

    private static Byte[] toBoxedByteArray(byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> bytes[i])
                .toArray(Byte[]::new);
    }

    private static byte[] toPrimitiveByteArray(Byte[] input) {
        byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = input[i];
        }
        return result;
    }

    public void retransformClasses(Class<?>[] classes, ClassFileTransformer classFileTransformer) {
        instrumentation.addTransformer(classFileTransformer, true);
        for (Class<?> tClass : classes) {
            try {
                instrumentation.retransformClasses(tClass);
            } catch (UnmodifiableClassException e) {
                LOGGER.error("Could not transform class {}", tClass.getName());
            }
        }
        instrumentation.removeTransformer(classFileTransformer);
    }

    public void redefineClass(Class<?> clazz, byte[] bytes) {
        try {
            instrumentation.redefineClasses(new ClassDefinition(clazz, bytes));
        } catch (ClassNotFoundException | UnmodifiableClassException e) {
            throw new RuntimeException("Could not redefine class " + clazz, e);
        }
    }

    public void transformClasses() {
        try {
            Class<?> transformerClass = Class.forName("org.spongepowered.tools.agent.MixinAgent$Transformer");

            Object transformerObject = ReflectionUtil.constructor(transformerClass)
                    .newInstance();

            CommonUtil.castSafe(ClassFileTransformer.class, transformerObject).ifPresent(transformer ->
                    retransformClasses(new Class<?>[]{}, transformer)
            );
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not transform class");
            throw new RuntimeException(e);
        }
    }

    public boolean isLoaded() {
        return this.instrumentation != null;
    }

    public void init(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }
}
