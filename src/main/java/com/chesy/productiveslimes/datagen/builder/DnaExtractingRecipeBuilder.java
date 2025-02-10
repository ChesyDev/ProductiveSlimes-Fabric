package com.chesy.productiveslimes.datagen.builder;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.recipe.DnaExtractingRecipe;
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

public class DnaExtractingRecipeBuilder implements CraftingRecipeJsonBuilder {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private int inputCount;
    private int energy;
    private float outputChance;
    private final List<ItemStack> outputs = new ArrayList<>();
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    public static DnaExtractingRecipeBuilder dnaExtractingRecipe() {
        return new DnaExtractingRecipeBuilder();
    }

    private DnaExtractingRecipeBuilder() {
        // Private constructor to enforce the use of the static factory method
    }

    public DnaExtractingRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public DnaExtractingRecipeBuilder setInputCount(int count) {
        this.inputCount = count;
        return this;
    }

    public DnaExtractingRecipeBuilder addOutput(ItemStack output) {
        this.outputs.add(output);
        return this;
    }

    public DnaExtractingRecipeBuilder setEnergy(int energy) {
        this.energy = energy;
        return this;
    }

    public DnaExtractingRecipeBuilder setOutputChance(float outputChance) {
        this.outputChance = outputChance;
        return this;
    }

    @Override
    public DnaExtractingRecipeBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public DnaExtractingRecipeBuilder group(@Nullable String group) {
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

        DnaExtractingRecipe recipe = new DnaExtractingRecipe(
                this.ingredients,
                this.outputs,
                this.inputCount,
                this.energy,
                this.outputChance
        );

        exporter.accept(recipeKey, recipe, advancement.build(Identifier.of(ProductiveSlimes.MODID, "recipes/" + recipeKey.getPath())));
    }
}
