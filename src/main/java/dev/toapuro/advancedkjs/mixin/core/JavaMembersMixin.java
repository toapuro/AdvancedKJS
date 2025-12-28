package dev.toapuro.advancedkjs.mixin.core;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.JavaMembers;
import dev.toapuro.advancedkjs.content.kubejs.wrappers.ReflectorStateHandler;
import dev.toapuro.advancedkjs.content.reflector.FieldExtraInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;

@SuppressWarnings("deprecation")
@Mixin(value = JavaMembers.class, remap = false)
public abstract class JavaMembersMixin {

    @Redirect(method = "getAccessibleMethods", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Modifier;isPublic(I)Z"))
    public boolean modifyMethodPublic(int mod, @Local Class<?> currentClass) {
        return true;
    }

    @Redirect(method = "getAccessibleFields", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Modifier;isPublic(I)Z"))
    public boolean modifyFieldPublic(int mod, @Local Class<?> currentClass) {
        return true;
    }

    @Inject(method = "getAccessibleMethods", at = @At(value = "INVOKE", target = "Ljava/util/LinkedHashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    public void getAccessibleMethods(Context cx, boolean includeProtected, CallbackInfoReturnable<Collection<JavaMembers.MethodInfo>> cir,
                                     @Local JavaMembers.MethodInfo info, @Local Method method) {
        if (!Modifier.isPublic(method.getModifiers())) {
            info.hidden = true;
        }
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    @Redirect(method = "getAccessibleFields", at = @At(value = "INVOKE", target = "Ljava/util/LinkedHashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    public Object getAccessibleFields(LinkedHashMap<String, JavaMembers.FieldInfo> instance, Object key, Object value,
                                      @Local JavaMembers.FieldInfo info, @Local Field field, @Local LinkedHashMap<String, JavaMembers.FieldInfo> fieldMap) {
        FieldExtraInfo extraInfo = new FieldExtraInfo(info, false);
        if (!Modifier.isPublic(field.getModifiers())) {
            extraInfo.hidden = true;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        fieldMap.put((String) key, extraInfo);
        return extraInfo;
    }

    @ModifyExpressionValue(method = "getAccessibleMethods", at = @At(value = "FIELD", target = "Ldev/latvian/mods/rhino/JavaMembers$MethodInfo;hidden:Z", ordinal = 1))
    public boolean modifyMethodHidden(boolean hidden) {
        return false;
    }

    @SuppressWarnings("CodeBlock2Expr")
    @ModifyReturnValue(method = "getAccessibleMethods", at = @At(value = "RETURN"))
    public Collection<JavaMembers.MethodInfo> modifyMethods(Collection<JavaMembers.MethodInfo> original) {
        return original.stream()
                .filter(methodInfo -> {
                    return !methodInfo.hidden || ReflectorStateHandler.isIgnoreInaccessible();
                })
                .toList();
    }

    @ModifyReturnValue(method = "getAccessibleFields", at = @At(value = "RETURN"))
    public Collection<JavaMembers.FieldInfo> modifyFields(Collection<JavaMembers.FieldInfo> original) {
        return original.stream()
                .filter(fieldInfo -> {
                    if (fieldInfo instanceof FieldExtraInfo extraInfo) {
                        return !extraInfo.hidden || ReflectorStateHandler.isIgnoreInaccessible();
                    }
                    return true;
                })
                .toList();
    }
}
