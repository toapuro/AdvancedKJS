package dev.toapuro.advancedkjs.mixin;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import dev.toapuro.advancedkjs.resolution.TypeJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = TypeWrappers.class, remap = false)
public class TypeWrappersMixin {
    @Inject(method = "getWrapperFactory", at = @At("HEAD"), cancellable = true)
    public void getWrapperFactory(Class<?> target, @Nullable Object from, CallbackInfoReturnable<TypeWrapperFactory<?>> cir) {
        if (target != Object.class) {
            Object fromObj = Context.jsToJava(ScriptManager.getCurrentContext(), from, Object.class);
            if (fromObj instanceof TypeJS typeJS) {
                if (typeJS.validateTarget(target)) {
                    cir.setReturnValue((context, ignored) -> typeJS.getValue());
                } else {
                    cir.setReturnValue(null);
                }
            }
        }
    }
}
