package dev.toapuro.advancedkjs.mixin;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.toapuro.advancedkjs.kubejs.event.DatagenRecipeRegisterEvent;
import dev.toapuro.advancedkjs.kubejs.events.DatagenEventsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = RecipesEventJS.class, remap = false)
public class RecipesEventJSMixin {
    @Inject(method = "post", at = @At(
            value = "INVOKE",
            target = "Ldev/latvian/mods/kubejs/event/EventHandler;post(Ldev/latvian/mods/kubejs/script/ScriptTypeHolder;Ldev/latvian/mods/kubejs/event/EventJS;)Ldev/latvian/mods/kubejs/event/EventResult;",
            shift = At.Shift.AFTER))
    public void afterPost(RecipeManager recipeManager, Map<ResourceLocation, JsonElement> datapackRecipeMap, CallbackInfo ci) {
        DatagenEventsJS.DATAGEN_RECIPE.post(ScriptType.SERVER, new DatagenRecipeRegisterEvent((RecipesEventJS) (Object) this));
    }
}
