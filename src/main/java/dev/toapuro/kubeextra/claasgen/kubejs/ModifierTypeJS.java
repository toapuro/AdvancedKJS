package dev.toapuro.kubeextra.claasgen.kubejs;

import javassist.Modifier;

public enum ModifierTypeJS {
    STATIC(Modifier.STATIC),
    FINAL(Modifier.FINAL),
    STATIC_FINAL(Modifier.STATIC | Modifier.FINAL);

    private final int modifiers;

    ModifierTypeJS(int modifiers) {
        this.modifiers = modifiers;
    }

    public int getModifiers() {
        return modifiers;
    }
}
