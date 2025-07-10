package dev.toapuro.advancedkjs.bytes.claasgen.kubejs.builder;

import java.lang.reflect.Modifier;

public interface IModifierBuilder<T extends IModifierBuilder<T>> {
    void applyModifier(int modifier);

    @SuppressWarnings("unchecked")
    default T priv() {
        applyModifier(Modifier.PRIVATE);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T pub() {
        applyModifier(Modifier.PUBLIC);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T prot() {
        applyModifier(Modifier.PROTECTED);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T statical() {
        applyModifier(Modifier.STATIC);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T finalized() {
        applyModifier(Modifier.FINAL);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T modifier(int modifier) {
        applyModifier(modifier);
        return (T) this;
    }
}
