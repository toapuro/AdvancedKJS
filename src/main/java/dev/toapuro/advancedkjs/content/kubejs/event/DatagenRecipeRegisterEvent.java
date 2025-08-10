package dev.toapuro.advancedkjs.content.kubejs.event;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class DatagenRecipeRegisterEvent extends EventJS {
    private final RecipesEventJS recipeEvent;
    public final Consumer<FinishedRecipe> writer = this::finalize;

    public DatagenRecipeRegisterEvent(RecipesEventJS recipeEvent) {
        this.recipeEvent = recipeEvent;
    }

    public void finalize(FinishedRecipe finishedRecipe) {
        JsonObject serialized = finishedRecipe.serializeRecipe();

        try {
            if (serialized.has("type")) {
                RecipeTypeFunction type = recipeEvent.getRecipeFunction(serialized.get("type").getAsString());
                if (type == null) {
                    throw new RecipeExceptionJS("Unknown recipe type: " + serialized.get("type").getAsString());
                } else {
                    RecipeJS recipe = type.schemaType.schema.deserialize(type, finishedRecipe.getId(), serialized);
                    recipe.afterLoaded();
                    recipeEvent.addRecipe(recipe, true);
                }
            } else {
                throw new RecipeExceptionJS("JSON must contain 'type'!");
            }
        } catch (RecipeExceptionJS rex) {
            if (rex.error) {
                throw rex;
            }
        }
    }
}
