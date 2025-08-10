package dev.toapuro.advancedkjs.content.kubejs.wrap;

import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class JavaWrapperFactories {
    private static final List<IWrapperFactory> wrappers = new ArrayList<>();

    public static void register(IWrapperFactory factory) {
        wrappers.add(factory);
    }

    @Nullable
    public static CallbackEvent<TypeWrapperFactory<?>> applyWrapper(Class<?> target, @Nullable Object from) {
        for (IWrapperFactory wrapper : wrappers) {
            CallbackEvent<TypeWrapperFactory<?>> result = new CallbackEvent<>();
            wrapper.apply(target, from, result);
            if (result.cancelled) {
                return result;
            }
        }
        return null;
    }

    public interface IWrapperFactory {
        void apply(Class<?> target, @Nullable Object from, CallbackEvent<TypeWrapperFactory<?>> event);
    }

    public static class CallbackEvent<T> {
        T result = null;
        boolean cancelled = false;

        CallbackEvent() {
        }

        public void setValue(T result) {
            this.result = result;
            this.cancelled = true;
        }

        public T getResult() {
            return result;
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }
}
