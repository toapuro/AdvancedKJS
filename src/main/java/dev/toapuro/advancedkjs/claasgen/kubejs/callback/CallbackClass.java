package dev.toapuro.advancedkjs.claasgen.kubejs.callback;

import java.util.ArrayList;
import java.util.List;

public final class CallbackClass {
    private final String className;
    private final List<CallbackMethod> methods;
    private final List<InstantFunction> instants;

    public CallbackClass(String className) {
        this.className = className;
        this.methods = new ArrayList<>();
        this.instants = new ArrayList<>();
    }

    public void addInstant(InstantFunction instantFunction) {
        this.instants.add(instantFunction);
    }

    public void addMethodCallback(CallbackMethod callbackMethod) {
        this.methods.add(callbackMethod);
    }

    public String getClassName() {
        return className;
    }

    public List<CallbackMethod> getMethods() {
        return methods;
    }

    public List<InstantFunction> getInstants() {
        return instants;
    }
}
