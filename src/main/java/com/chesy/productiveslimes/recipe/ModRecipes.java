package com.chesy.productiveslimes.recipe;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.screen.custom.GuidebookMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModRecipes {

    public static final RecipeSerializer<?> MELTING_SERIALIZER =
            registerSerializer("melting", MeltingRecipe.Serializer.INSTANCE);

    public static final RecipeType<MeltingRecipe> MELTING_TYPE =
            registerType("melting");

    public static final RecipeSerializer<?> SOLIDING_SERIALIZER =
            registerSerializer("soliding", SolidingRecipe.Serializer.INSTANCE);

    public static final RecipeType<SolidingRecipe> SOLIDING_TYPE =
            registerType("soliding");

    private static RecipeSerializer registerSerializer(String name, RecipeSerializer<?> recipe) {
        return RecipeSerializer.register(name, recipe);
    }

    private static RecipeType registerType(String name) {
        return RecipeType.register(name);
    }

    public static void register() {

    }
}
