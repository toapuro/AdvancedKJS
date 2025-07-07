package dev.toapuro.advancedkjs.util.builder;

import java.util.ArrayList;
import java.util.List;

public abstract class ParamBuilder<SELF extends ParamBuilder<SELF>> {
    private final List<Class<?>> paramTypes;
    private final List<Object> param;

    public ParamBuilder() {
        this.paramTypes = new ArrayList<>();
        this.param = new ArrayList<>();
    }

    public abstract SELF self();

    public <U, T extends U> SELF param(Class<U> paramClass , T tValue) {
        this.paramTypes.add(paramClass);
        this.param.add(tValue);
        return self();
    }

    protected List<Class<?>> getParamTypes() {
        return paramTypes;
    }

    protected List<Object> getParam() {
        return param;
    }
}
