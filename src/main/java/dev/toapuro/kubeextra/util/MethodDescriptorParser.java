package dev.toapuro.kubeextra.util;

import javassist.CtClass;

public class MethodDescriptorParser {
    public record MethodSignature(String methodName, String descriptor) {
    }

    public static MethodSignature parseMethodWithDescriptor(String methodWithDescriptor) {
        int parenIndex = methodWithDescriptor.indexOf('(');
        if (parenIndex == -1) {
            throw new IllegalArgumentException("Invalid method descriptor format: " + methodWithDescriptor);
        }

        String methodName = methodWithDescriptor.substring(0, parenIndex);
        String descriptor = methodWithDescriptor.substring(parenIndex);

        return new MethodSignature(methodName, descriptor);
    }
}
