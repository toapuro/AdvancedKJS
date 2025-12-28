package dev.toapuro.advancedkjs.mixin.core;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.latvian.mods.rhino.*;
import dev.toapuro.advancedkjs.content.kubejs.wrappers.ReflectorJS;
import dev.toapuro.advancedkjs.content.kubejs.wrappers.ReflectorStateHandler;
import dev.toapuro.advancedkjs.mixin.core.accessor.JavaMembersAccessor;
import dev.toapuro.advancedkjs.mixin.helper.MixinUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = NativeJavaObject.class, remap = false)
public abstract class NativeJavaObjectMixin {

    @Shadow
    protected transient Object javaObject;
    @Shadow
    protected transient Class<?> staticType;

    @Shadow
    protected transient JavaMembers members;
    @Shadow
    protected transient boolean isAdapter;
    @Shadow
    protected transient Map<String, FieldAndMethods> fieldAndMethods;
    @Shadow
    protected transient Map<String, Object> customMembers;

    @Unique
    private static JavaMembers akjs$lookupReflectedClass(Context cx, Scriptable scope, Class<?> dynamicType, Class<?> staticType, boolean includeProtected) {
        Class<?> cl = dynamicType;

        while (true) {
            try {
                return JavaMembersAccessor.invokeNew(cl, includeProtected, cx, ScriptableObject.getTopLevelScope(scope));
            } catch (SecurityException e) {
                if (staticType != null && staticType.isInterface()) {
                    cl = staticType;
                    staticType = null;
                    continue;
                }

                Class<?> parent = cl.getSuperclass();
                if (parent == null) {
                    if (!cl.isInterface()) {
                        throw e;
                    }

                    parent = ScriptRuntime.ObjectClass;
                }

                cl = parent;
            }
        }
    }

    @WrapOperation(method = "<init>(Ldev/latvian/mods/rhino/Scriptable;Ljava/lang/Object;Ljava/lang/Class;ZLdev/latvian/mods/rhino/Context;)V",
            at = @At(value = "INVOKE", target = "Ldev/latvian/mods/rhino/NativeJavaObject;initMembers(Ldev/latvian/mods/rhino/Context;Ldev/latvian/mods/rhino/Scriptable;)V"))
    public void init(NativeJavaObject instance, Context context, Scriptable scope, Operation<Void> operation) {
        if (this.javaObject instanceof ReflectorJS reflectorJS) {
            this.javaObject = reflectorJS.unwrap();
            this.staticType = reflectorJS.unwrap().getClass();

            ReflectorStateHandler.setIgnoreInaccessible(true);

            try {
                akjs$initReflectedMembers(context, scope);
            } finally {
                ReflectorStateHandler.setIgnoreInaccessible(false);
            }
        } else {
            operation.call(instance, context, scope);
        }
    }

    @Unique
    protected void akjs$initReflectedMembers(Context cx, Scriptable scope) {
        Class<?> dynamicType;
        if (this.javaObject != null) {
            dynamicType = this.javaObject.getClass();
        } else {
            dynamicType = this.staticType;
        }

        this.members = akjs$lookupReflectedClass(cx, scope, dynamicType, this.staticType, this.isAdapter);
        this.fieldAndMethods = this.members.getFieldAndMethodsObjects(
                MixinUtil.cast(this), this.javaObject, false, cx);
        this.customMembers = null;
    }

    @Inject(method = "unwrap", at = @At("HEAD"), cancellable = true)
    private void unwrapped(CallbackInfoReturnable<Object> cir) {
        if (javaObject instanceof ReflectorJS reflectorJS) {
            cir.setReturnValue(reflectorJS.unwrap());
        }
    }
}
