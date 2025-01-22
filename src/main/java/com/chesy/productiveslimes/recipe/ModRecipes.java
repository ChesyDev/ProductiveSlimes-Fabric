package com.chesy.productiveslimes.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;


public class ModRecipes {

    public static final RecipeSerializer<MeltingRecipe> MELTING_SERIALIZER = registerSerializer("melting", MeltingRecipe.Serializer.INSTANCE);
    public static final RecipeType<MeltingRecipe> MELTING_TYPE = registerType("melting");

    public static final RecipeSerializer<SolidingRecipe> SOLIDING_SERIALIZER = registerSerializer("soliding", SolidingRecipe.Serializer.INSTANCE);
    public static final RecipeType<SolidingRecipe> SOLIDING_TYPE = registerType("soliding");

    public static final RecipeSerializer<DnaExtractingRecipe> DNA_EXTRACTING_SERIALIZER = registerSerializer("dna_extracting", DnaExtractingRecipe.Serializer.INSTANCE);
    public static final RecipeType<DnaExtractingRecipe> DNA_EXTRACTING_TYPE = registerType("dna_extracting");

    public static final RecipeSerializer<DnaSynthesizingRecipe> DNA_SYNTHESIZING_SERIALIZER = registerSerializer("dna_synthesizing", DnaSynthesizingRecipe.Serializer.INSTANCE);
    public static final RecipeType<DnaSynthesizingRecipe> DNA_SYNTHESIZING_TYPE = registerType("dna_synthesizing");

    public static final RecipeSerializer<SqueezingRecipe> SQUEEZING_SERIALIZER = registerSerializer("squeezing", SqueezingRecipe.Serializer.INSTANCE);
    public static final RecipeType<SqueezingRecipe> SQUEEZING_TYPE = registerType("squeezing");

    private static <T extends Recipe<?>> RecipeSerializer<T> registerSerializer(String name, RecipeSerializer<T> recipe) {
        return RecipeSerializer.register(name, recipe);
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(String name) {
        return RecipeType.register(name);
    }

    public static void register() {

    }
}
