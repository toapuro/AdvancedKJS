package dev.toapuro.kubeextra.claasgen.kubejs;

import javassist.Modifier;

public enum AccessTypeJS {
    PUBLIC(Modifier.PUBLIC),
    PRIVATE(Modifier.PRIVATE),
    PROTECTED(Modifier.PROTECTED);

    private final int modifiers;

    AccessTypeJS(int modifiers) {
        this.modifiers = modifiers;
    }

    public int getModifiers() {
        return modifiers;
    }
}
