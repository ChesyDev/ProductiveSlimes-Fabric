package com.chesy.productiveslimes.compat.jei;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.recipe.*;
import com.chesy.productiveslimes.screen.custom.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class JEPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.of(ProductiveSlimes.MODID,"jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MeltingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SolidingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new DnaExtractingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new DnaSynthesizingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SqueezingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = MinecraftClient.getInstance().world.getRecipeManager();

        List<RecipeEntry<MeltingRecipe>> meltingRecipes = recipeManager.listAllOfType(ModRecipes.MELTING_TYPE);
        List<MeltingRecipe> meltingRecipeList = meltingRecipes.stream().map(RecipeEntry::value).collect(Collectors.toList());

        List<RecipeEntry<SolidingRecipe>> solidingRecipes = recipeManager.listAllOfType(ModRecipes.SOLIDING_TYPE);
        List<SolidingRecipe> solidingRecipeList = solidingRecipes.stream().map(RecipeEntry::value).collect(Collectors.toList());

        List<RecipeEntry<DnaExtractingRecipe>> dnaExtractingRecipes = recipeManager.listAllOfType(ModRecipes.DNA_EXTRACTING_TYPE);
        List<DnaExtractingRecipe> dnaExtractingRecipeList = dnaExtractingRecipes.stream().map(RecipeEntry::value).collect(Collectors.toList());

        List<RecipeEntry<DnaSynthesizingRecipe>> dnaSynthesizingRecipes = recipeManager.listAllOfType(ModRecipes.DNA_SYNTHESIZING_TYPE);
        List<DnaSynthesizingRecipe> dnaSynthesizingRecipeList = dnaSynthesizingRecipes.stream().map(RecipeEntry::value).collect(Collectors.toList());

        List<RecipeEntry<SqueezingRecipe>> squeezingRecipes = recipeManager.listAllOfType(ModRecipes.SQUEEZING_TYPE);
        List<SqueezingRecipe> squeezingRecipeList = squeezingRecipes.stream().map(RecipeEntry::value).collect(Collectors.toList());

        registration.addRecipes(MeltingCategory.MELTING_TYPE, meltingRecipeList);
        registration.addRecipes(SolidingCategory.SOLIDING_TYPE, solidingRecipeList);
        registration.addRecipes(DnaExtractingCategory.DNA_EXTRACTING_TYPE, dnaExtractingRecipeList);
        registration.addRecipes(DnaSynthesizingCategory.DNA_SYNTHESIZING_TYPE, dnaSynthesizingRecipeList);
        registration.addRecipes(SqueezingCategory.SQUEEZING_TYPE, squeezingRecipeList);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(MeltingStationScreen.class, 77, 38, 26, 8, MeltingCategory.MELTING_TYPE);
        registration.addRecipeClickArea(SolidingStationScreen.class, 77, 38, 26, 8, SolidingCategory.SOLIDING_TYPE);
        registration.addRecipeClickArea(DnaExtractorScreen.class, 77, 38, 26, 8, DnaExtractingCategory.DNA_EXTRACTING_TYPE);
        registration.addRecipeClickArea(DnaSynthesizerScreen.class, 77, 38, 26, 8, DnaSynthesizingCategory.DNA_SYNTHESIZING_TYPE);
        registration.addRecipeClickArea(SlimeSqueezerScreen.class, 77, 38, 26, 8, SqueezingCategory.SQUEEZING_TYPE);
    }
}