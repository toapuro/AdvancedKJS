package dev.toapuro.kubeextra.mixin;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.toapuro.kubeextra.claasgen.ClassLoaderHandler;
import dev.toapuro.kubeextra.claasgen.KubeExtraClassLoader;
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
        KubeExtraClassLoader currentClassLoader = ClassLoaderHandler.getCurrentClassLoader();
        Map<String, Class<?>> classMap = currentClassLoader.getClassLookup();
        if(classMap.containsKey(name)) {
            cir.setReturnValue(
                    new NativeJavaClass(this.context, this.topLevelScope, classMap.get(name))
            );
        }
    }
}
