package dev.toapuro.advancedkjs.claasgen.kubejs.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ImplContext {
    private final Map<String, CallbackClass> classCallbackMap;

    public ImplContext() {
        this.classCallbackMap = new HashMap<>();
    }

    public ImplContext(Map<String, CallbackClass> classCallbackMap) {
        this.classCallbackMap = classCallbackMap;
    }

    public void putCallback(String className, CallbackClass callbackClass) {
        this.classCallbackMap.put(className, callbackClass);
    }

    public void ifPresent(String className, Consumer<CallbackClass> consumer) {
        if (this.classCallbackMap.containsKey(className)) {
            consumer.accept(classCallbackMap.get(className));
        }
    }

    public Map<String, CallbackClass> getClassCallbackMap() {
        return classCallbackMap;
    }
}
