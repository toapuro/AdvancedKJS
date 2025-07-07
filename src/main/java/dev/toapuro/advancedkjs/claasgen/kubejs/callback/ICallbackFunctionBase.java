package dev.toapuro.advancedkjs.claasgen.kubejs.callback;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Stream;

public interface ICallbackFunctionBase {
    Logger LOGGER = LoggerFactory.getLogger(ICallbackFunctionBase.class);

    Object callFunctionRaw(Context context, Scriptable scope, Object instance, Object[] args);

    default Object call(Context context, Object instance, Object[] args) {
        if (context == null) {
            LOGGER.error("Context is null");
            return null;
        }

        Scriptable scope = context.getTopCallScope();


        Stream<?> functionArgs = Stream.concat(Stream.of(instance), Arrays.stream(args));

        Object[] wrappedArgs = functionArgs
                .map(arg -> Context.javaToJS(context, arg, scope))
                .toArray();

        Object returnValue = callFunctionRaw(context, context.getTopCallScope(), null, wrappedArgs);

        if (returnValue == null) {
            return null;
        }

        return Context.jsToJava(context, returnValue, Object.class);
    }
}
