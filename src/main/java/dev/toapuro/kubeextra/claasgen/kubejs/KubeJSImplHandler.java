package dev.toapuro.kubeextra.claasgen.kubejs;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.Context;
import dev.toapuro.kubeextra.claasgen.kubejs.callback.CallbackClass;
import dev.toapuro.kubeextra.claasgen.kubejs.callback.CallbackMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KubeJSImplHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubeJSImplHandler.class);
    private static final Map<String, CallbackClass> classImplMap = new HashMap<>();

    public static void handleMethodCall(Object instance, String className, String methodName, Object[] args) {
        Context currentContext = ScriptManager.getCurrentContext();
        if (classImplMap.containsKey(className)) {
            CallbackClass callbackClass = classImplMap.get(className);
            for (CallbackMethod method : callbackClass.methods()) {
                if(Objects.equals(method.methodName(), methodName)) {
                    method.call(currentContext, instance, args);
                }
            }
        } else {
            LOGGER.info("Called unknown method {} {}", className, methodName);
        }
    }

    public static void clearClassImplMap() {
        classImplMap.clear();
    }

    public static void addClassImpl(String className, CallbackClass callback) {
        classImplMap.put(className, callback);
    }
}
