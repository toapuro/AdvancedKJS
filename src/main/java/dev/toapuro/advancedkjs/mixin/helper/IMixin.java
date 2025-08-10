package dev.toapuro.advancedkjs.mixin.helper;

public interface IMixin<T> {
    @SuppressWarnings("unchecked")
    default T castSelf() {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default <S> IMixinAccessor<S> access(S o) {
        return (IMixinAccessor<S>) o;
    }
}
