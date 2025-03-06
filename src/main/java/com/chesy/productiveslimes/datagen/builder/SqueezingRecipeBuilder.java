package com.chesy.productiveslimes.datagen.builder;

import com.chesy.productiveslimes.recipe.SqueezingRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SqueezingRecipeBuilder implements CraftingRecipeJsonBuilder {
    private Ingredient ingredients;
    private int energy;
    private final List<ItemStack> outputs = new ArrayList<>();
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    public static SqueezingRecipeBuilder squeezingRecipe() {
        return new SqueezingRecipeBuilder();
    }

    private SqueezingRecipeBuilder() {
        // Private constructor to enforce the use of the static method
    }

    public SqueezingRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredients = ingredient;
        return this;
    }

    public SqueezingRecipeBuilder addOutput(ItemStack output) {
        this.outputs.add(output);
        return this;
    }

    public SqueezingRecipeBuilder setEnergy(int energy) {
        this.energy = energy;
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.outputs.isEmpty() ? Items.AIR : this.outputs.get(0).getItem();
    }

    @Override
    public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
        Advancement.Builder advancement = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(advancement::criterion);

        SqueezingRecipe recipe = new SqueezingRecipe(
                this.ingredients,
                this.outputs,
                this.energy
        );
        // Pass the recipe and advancement to the output
        exporter.accept(recipeKey, recipe, advancement.build(Identifier.of(recipeKey.getValue().getNamespace(), "recipes/" + recipeKey.getValue().getPath())));
    }
}
