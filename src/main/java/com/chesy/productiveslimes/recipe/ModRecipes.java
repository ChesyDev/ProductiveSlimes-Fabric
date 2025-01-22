package com.chesy.productiveslimes.recipe;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


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
        return Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(ProductiveSlimes.MODID, name), recipe);
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(String name) {
        return Registry.register(Registries.RECIPE_TYPE, Identifier.of(ProductiveSlimes.MODID, name), new RecipeType<T>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }

    public static void register() {

    }
}
