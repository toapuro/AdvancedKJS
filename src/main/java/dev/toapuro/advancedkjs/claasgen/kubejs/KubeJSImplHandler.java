package dev.toapuro.advancedkjs.claasgen.kubejs;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.Context;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.CallbackClass;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.CallbackMethod;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.ImplContext;
import dev.toapuro.advancedkjs.claasgen.kubejs.callback.InstantFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class KubeJSImplHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubeJSImplHandler.class);
    private static ImplContext implContext = new ImplContext();

    @SuppressWarnings("unused")
    public static Object handleMethodCall(Object instance, String className, String methodName, Object[] args) {
        Context currentContext = ScriptManager.getCurrentContext();

        Map<String, CallbackClass> classCallbackMap = implContext.getClassCallbackMap();
        if (classCallbackMap.containsKey(className)) {
            CallbackClass callbackClass = classCallbackMap.get(className);
            for (CallbackMethod method : callbackClass.getMethods()) {
                if (Objects.equals(method.name() + method.descriptor(), methodName)) {
                    return method.call(currentContext, instance, args);
                }
            }
        }


        LOGGER.error("Called unknown method {} {}", className, methodName);
        return null;
    }

    @SuppressWarnings("unused")
    public static Object handleInstantCall(Object instance, String className, int instantId, Object[] args) {
        Context currentContext = ScriptManager.getCurrentContext();
        Map<String, CallbackClass> classCallbackMap = implContext.getClassCallbackMap();
        if (classCallbackMap.containsKey(className)) {
            CallbackClass callbackClass = classCallbackMap.get(className);
            for (InstantFunction instant : callbackClass.getInstants()) {
                if (Objects.equals(instant.instantId(), instantId)) {
                    return instant.call(currentContext, instance, args);
                }
            }
        }
        LOGGER.info("Called unknown instant {} {}", className, instantId);
        return null;
    }

    public static void setImplContext(ImplContext newContext) {
        implContext = newContext;
    }
}
