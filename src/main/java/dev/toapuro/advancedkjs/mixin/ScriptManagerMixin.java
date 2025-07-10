package dev.toapuro.advancedkjs.mixin;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.advancedkjs.bytes.claasgen.handler.AdvancedKJSClassLoader;
import dev.toapuro.advancedkjs.bytes.claasgen.handler.ClassLoaderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = ScriptManager.class, remap = false)
public class ScriptManagerMixin {
    @Shadow public Scriptable topLevelScope;

    @Shadow public Context context;

    @Inject(method = "loadJavaClass", at = @At("HEAD"), cancellable = true)
    public void loadJavaClass(String name, boolean error, CallbackInfoReturnable<NativeJavaClass> cir) {
        AdvancedKJSClassLoader currentClassLoader = ClassLoaderHandler.getCurrentClassLoader();
        if (currentClassLoader == null) return;
        Map<String, Class<?>> classMap = currentClassLoader.getGenClassLookup();
        if(classMap.containsKey(name)) {
            cir.setReturnValue(
                    new NativeJavaClass(this.context, this.topLevelScope, classMap.get(name))
            );
        }
    }
}
