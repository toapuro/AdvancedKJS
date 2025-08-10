package dev.toapuro.advancedkjs.util.builder;

import java.util.ArrayList;
import java.util.List;

public abstract class ParamBuilder<BUILDER extends ParamBuilder<BUILDER>> {
    private final List<Class<?>> paramTypes;
    private final List<Object> param;

    public ParamBuilder() {
        this.paramTypes = new ArrayList<>();
        this.param = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public <U, T extends U> BUILDER param(Class<U> paramClass, T tValue) {
        this.paramTypes.add(paramClass);
        this.param.add(tValue);
        return (BUILDER) this;
    }

    protected List<Class<?>> getParamTypes() {
        return paramTypes;
    }

    protected List<Object> getParam() {
        return param;
    }
}
