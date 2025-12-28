package dev.toapuro.advancedkjs.mixin.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.toapuro.advancedkjs.content.kubejs.group.DatagenEventsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = RecipeManager.class, priority = 1099)
public class RecipeManagerMixin {
    @Inject(method = "apply*", at = @At("HEAD"), cancellable = true)
    private void customRecipesHead(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        if (!ServerEvents.RECIPES.hasListeners() && DatagenEventsJS.DATAGEN_RECIPE.hasListeners()) {
            if (RecipesEventJS.instance != null) {
                RecipesEventJS.instance.post(UtilsJS.cast(this), map);
                RecipesEventJS.instance = null;
                ci.cancel();
            } else {
                ConsoleJS.SERVER.warn("RecipeManagerMixin: RecipesEventJS.instance is null, falling back to vanilla!");
            }
        }
    }
}
