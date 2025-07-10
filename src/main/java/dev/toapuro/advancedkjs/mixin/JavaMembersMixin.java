package dev.toapuro.advancedkjs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.JavaMembers;
import dev.toapuro.advancedkjs.bytes.reflector.FieldExtraInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;

@Mixin(value = JavaMembers.class, remap = false)
public abstract class JavaMembersMixin {
    @Inject(method = "getAccessibleMethods", at = @At(value = "INVOKE", target = "Ljava/util/LinkedHashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    public void getAccessibleMethods(Context cx, boolean includeProtected, CallbackInfoReturnable<Collection<JavaMembers.MethodInfo>> cir,
                                     @Local JavaMembers.MethodInfo info, @Local Method method) {
        if (!Modifier.isPublic(method.getModifiers())) {
            info.hidden = true;
        }
    }

    @Redirect(method = "getAccessibleMethods", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Modifier;isPublic(I)Z"))
    public boolean modifyMethodPublic(int mod, @Local Class<?> currentClass) {
        return true;
    }

    @Redirect(method = "getAccessibleFields", at = @At(value = "INVOKE", target = "Ljava/util/LinkedHashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    public Object getAccessibleFields(LinkedHashMap<String, JavaMembers.FieldInfo> instance, Object key, Object value,
                                      @Local JavaMembers.FieldInfo info, @Local Field field, @Local LinkedHashMap<String, JavaMembers.FieldInfo> fieldMap) {
        FieldExtraInfo extraInfo = new FieldExtraInfo(info.field, false);
        extraInfo.name  = info.name;

        if (!Modifier.isPublic(field.getModifiers())) {
            extraInfo.hidden = true;
        }

        fieldMap.put((String) key, extraInfo);
        return extraInfo;
    }

    @Redirect(method = "getAccessibleFields", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Modifier;isPublic(I)Z"))
    public boolean modifyFieldPublic(int mod, @Local Class<?> currentClass) {
        return true;
    }
}
