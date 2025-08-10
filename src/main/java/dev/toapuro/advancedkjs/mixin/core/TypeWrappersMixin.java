package dev.toapuro.advancedkjs.mixin.core;

import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import dev.toapuro.advancedkjs.content.kubejs.wrap.JavaWrapperFactories;
import dev.toapuro.advancedkjs.mixin.helper.IMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = TypeWrappers.class, remap = false)
public class TypeWrappersMixin implements IMixin<TypeWrappers> {
    @Inject(method = "getWrapperFactory", at = @At("HEAD"), cancellable = true)
    public void getWrapperFactory(Class<?> target, @Nullable Object from, CallbackInfoReturnable<TypeWrapperFactory<?>> cir) {
        if (target != Object.class) {
            var event = JavaWrapperFactories.applyWrapper(target, from);
            if (event != null && event.isCancelled()) {
                cir.setReturnValue(event.getResult());
            }
        }
    }
}
