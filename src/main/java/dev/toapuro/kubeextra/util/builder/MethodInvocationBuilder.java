package dev.toapuro.kubeextra.util.builder;


import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class MethodInvocationBuilder<C> extends ParamBuilder<MethodInvocationBuilder<C>> {
    private boolean declaredMethod = false;
    private final Class<C> clazz;
    private final String methodName;

    public MethodInvocationBuilder(Class<C> clazz, String methodName) {
        this.clazz = clazz;
        this.methodName = methodName;
    }

    public MethodInvocationBuilder<C> declaredMethod() {
        this.declaredMethod = true;
        return this;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T invoke(C obj) {
        Class<?>[] parameterTypeArray = getParamTypes().toArray(Class<?>[]::new);
        Object[] parameterArray = getParam().toArray(Object[]::new);

        try {
            Method method = declaredMethod ? clazz.getDeclaredMethod(methodName, parameterTypeArray) : clazz.getMethod(methodName, parameterTypeArray);
            method.setAccessible(true);
            return (T) method.invoke(obj, parameterArray);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

    public <T> Optional<T> invokeSafe(C obj) {
        return Optional.ofNullable(invoke(null));
    }

    public <T> T invokeStatic() {
        return invoke(null);
    }

    public <T> Optional<T> invokeStaticSafe() {
        return Optional.ofNullable(invoke(null));
    }

    @Override
    public MethodInvocationBuilder<C> self() {
        return this;
    }
}
