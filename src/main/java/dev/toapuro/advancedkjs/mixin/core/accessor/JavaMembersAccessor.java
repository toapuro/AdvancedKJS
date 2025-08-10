package dev.toapuro.advancedkjs.mixin.core.accessor;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.JavaMembers;
import dev.latvian.mods.rhino.Scriptable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JavaMembers.class)
public interface JavaMembersAccessor {
    @Invoker("<init>")
    static JavaMembers invokeNew(Class<?> cl, boolean includeProtected, Context cx, Scriptable scope) {
        return null;
    }
}
