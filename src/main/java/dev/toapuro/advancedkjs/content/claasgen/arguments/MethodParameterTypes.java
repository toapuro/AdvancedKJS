package dev.toapuro.advancedkjs.content.claasgen.arguments;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MethodParameterTypes extends ArrayList<MethodParameterType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodParameterTypes.class);

    public MethodParameterTypes() {
    }

    public CtClass[] getCtArrayParams() {
        return stream()
                .map(MethodParameterType::getCtClassParam)
                .toArray(CtClass[]::new);
    }

    public static MethodParameterTypes fromDescriptor(String descriptor) {
        try {
            CtClass[] parameterTypes = Descriptor.getParameterTypes(descriptor, ClassPool.getDefault());
            MethodParameterTypes types = new MethodParameterTypes();
            for (CtClass parameterType : parameterTypes) {
                types.add(new MethodParameterType(parameterType));
            }
            return types;
        } catch (NotFoundException e) {
            LOGGER.error("Error occurred parsing descriptor", e);
            throw new RuntimeException(e);
        }
    }

}
