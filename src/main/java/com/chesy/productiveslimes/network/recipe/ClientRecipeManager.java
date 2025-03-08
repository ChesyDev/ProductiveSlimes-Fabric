package com.chesy.productiveslimes.network.recipe;

import com.chesy.productiveslimes.recipe.*;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

import java.util.*;

public class ClientRecipeManager {
    public static final Map<Identifier, RecipeEntry<?>> CLIENT_RECIPES = new HashMap<>();
    public static final List<DnaExtractingRecipe> DNA_EXTRACTING_RECIPES = new ArrayList<>();
    public static final List<DnaSynthesizingRecipe> DNA_SYNTHESIZING_RECIPES = new ArrayList<>();
    public static final List<MeltingRecipe> MELTING_RECIPES = new ArrayList<>();
    public static final List<SolidingRecipe> SOLIDING_RECIPES = new ArrayList<>();
    public static final List<SqueezingRecipe> SQUEEZING_RECIPES = new ArrayList<>();

    public static void updateRecipes(List<RecipeEntry<?>> recipes) {
        CLIENT_RECIPES.clear();
        DNA_EXTRACTING_RECIPES.clear();
        DNA_SYNTHESIZING_RECIPES.clear();
        MELTING_RECIPES.clear();
        SOLIDING_RECIPES.clear();
        SQUEEZING_RECIPES.clear();

        for (RecipeEntry<?> recipeHolder : recipes) {
            CLIENT_RECIPES.put(recipeHolder.id().getValue(), recipeHolder);
            if (recipeHolder.value() instanceof DnaExtractingRecipe dnaExtractingRecipe) {
                DNA_EXTRACTING_RECIPES.add(dnaExtractingRecipe);
            } else if (recipeHolder.value() instanceof DnaSynthesizingRecipe dnaSynthesizingRecipe) {
                DNA_SYNTHESIZING_RECIPES.add(dnaSynthesizingRecipe);
            } else if (recipeHolder.value() instanceof MeltingRecipe meltingRecipe) {
                MELTING_RECIPES.add(meltingRecipe);
            } else if (recipeHolder.value() instanceof SolidingRecipe solidingRecipe) {
                SOLIDING_RECIPES.add(solidingRecipe);
            } else if (recipeHolder.value() instanceof SqueezingRecipe squeezingRecipe) {
                SQUEEZING_RECIPES.add(squeezingRecipe);
            }
        }
    }

    public static Optional<RecipeEntry<?>> getRecipe(Identifier id) {
        return Optional.ofNullable(CLIENT_RECIPES.get(id));
    }

    public static int getAllRecipes() {
        return CLIENT_RECIPES.size();
    }

    public static List<DnaExtractingRecipe> getDnaExtractingRecipes() {
        return DNA_EXTRACTING_RECIPES;
    }

    public static List<DnaSynthesizingRecipe> getDnaSynthesizingRecipes() {
        return DNA_SYNTHESIZING_RECIPES;
    }

    public static List<MeltingRecipe> getMeltingRecipes() {
        return MELTING_RECIPES;
    }

    public static List<SolidingRecipe> getSolidingRecipes() {
        return SOLIDING_RECIPES;
    }

    public static List<SqueezingRecipe> getSqueezingRecipes() {
        return SQUEEZING_RECIPES;
    }
}
