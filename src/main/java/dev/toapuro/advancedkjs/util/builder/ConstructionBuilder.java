package dev.toapuro.advancedkjs.util.builder;


import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ConstructionBuilder<C> extends ParamBuilder<ConstructionBuilder<C>> {
    private boolean declaredConstructor = false;
    private final Class<C> clazz;

    public ConstructionBuilder(Class<C> clazz) {
        this.clazz = clazz;
    }

    public ConstructionBuilder<C> declaredMethod() {
        this.declaredConstructor = true;
        return this;
    }

    @Nullable
    public C newInstance() {
        Class<?>[] parameterTypeArray = getParamTypes().toArray(Class<?>[]::new);
        Object[] parameterArray = getParam().toArray(Object[]::new);

        try {
            Constructor<C> ctor = declaredConstructor ? clazz.getDeclaredConstructor(parameterTypeArray) : clazz.getConstructor(parameterTypeArray);
            ctor.setAccessible(true);
            return ctor.newInstance(parameterArray);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            return null;
        }
    }

    public Optional<C> newInstanceSafe() {
        return Optional.ofNullable(newInstance());
    }

    @Override
    public ConstructionBuilder<C> self() {
        return this;
    }
}
