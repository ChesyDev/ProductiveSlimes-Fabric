package com.chesy.productiveslimes.datagen.builder;

import com.chesy.productiveslimes.recipe.DnaSynthesizingRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
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

public class DnaSynthesizingRecipeBuilder implements CraftingRecipeJsonBuilder {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private int energy;
    private int inputCount;
    private final List<ItemStack> outputs = new ArrayList<>();
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    public static DnaSynthesizingRecipeBuilder dnaSynthesizingRecipe() {
        return new DnaSynthesizingRecipeBuilder();
    }

    private DnaSynthesizingRecipeBuilder() {
        // Private constructor to enforce the use of the static factory method
    }

    public DnaSynthesizingRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public DnaSynthesizingRecipeBuilder addOutput(ItemStack output) {
        this.outputs.add(output);
        return this;
    }

    public DnaSynthesizingRecipeBuilder setEnergy(int energy) {
        this.energy = energy;
        return this;
    }

    public DnaSynthesizingRecipeBuilder setInputCount(int inputCount) {
        this.inputCount = inputCount;
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
    public void offerTo(RecipeExporter exporter, Identifier recipeKey) {
        Advancement.Builder advancement = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(advancement::criterion);

        // Create the recipe instance
        DnaSynthesizingRecipe recipe = new DnaSynthesizingRecipe(
                this.ingredients,
                this.outputs,
                this.energy,
                this.inputCount
        );

        // Pass the recipe and advancement to the output
        exporter.accept(recipeKey, recipe, advancement.build(recipeKey.withPrefixedPath("recipes/")));
    }
}
