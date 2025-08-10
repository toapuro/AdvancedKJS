package dev.toapuro.advancedkjs.mixin.core.accessor;

import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptPack;
import dev.latvian.mods.kubejs.script.ScriptSource;
import dev.toapuro.advancedkjs.mixin.helper.IMixinAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ScriptManager.class, remap = false)
public interface ScriptManagerAccessor extends IMixinAccessor<ScriptManager> {
    @Invoker("loadFile")
    void invokeLoadFile(ScriptPack pack, ScriptFileInfo fileInfo, ScriptSource source);
}
