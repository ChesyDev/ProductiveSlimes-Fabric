package com.chesy.productiveslimes.datagen.builder;

import com.chesy.productiveslimes.recipe.DnaExtractingRecipe;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DnaExtractingRecipeBuilder implements CraftingRecipeJsonBuilder {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private int inputCount;
    private int energy;
    private float outputChance;
    private final List<ItemStack> outputs = new ArrayList<>();
    private final Map<String, CriterionConditions> criteria = new LinkedHashMap<>();
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
    public DnaExtractingRecipeBuilder criterion(String name, CriterionConditions criterion) {
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
    public void offerTo(Consumer<RecipeJsonProvider> consumer, Identifier resourceLocation) {
        consumer.accept(new Result(resourceLocation, ingredients, outputs, inputCount, energy, outputChance));
    }

    public static class Result implements RecipeJsonProvider{
        private final Identifier id;
        private final List<Ingredient> ingredients;
        private final List<ItemStack> outputs;
        private final int inputCount;
        private final int energy;
        private final float outputChance;

        public Result(Identifier id, List<Ingredient> ingredients, List<ItemStack> outputs, int inputCount, int energy, float outputChance) {
            this.id = id;
            this.ingredients = ingredients;
            this.outputs = outputs;
            this.inputCount = inputCount;
            this.energy = energy;
            this.outputChance = outputChance;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            jsonObject.addProperty("type", "productiveslimes:dna_extracting");
            jsonObject.addProperty("energy", energy);

            JsonArray ingredientArray = new JsonArray();
            for (Ingredient ingredient : ingredients) {
                ingredientArray.add(ingredient.toJson());
            }
            jsonObject.add("ingredients", ingredientArray);
            jsonObject.addProperty("inputCount", inputCount);

            JsonArray outputArray = new JsonArray();
            for (ItemStack output : outputs) {
                JsonObject outputJson = new JsonObject();
                outputJson.addProperty("item", output.getTranslationKey().substring(output.getTranslationKey().indexOf(".") + 1).replace('.', ':'));
                outputJson.addProperty("count", output.getCount());
                outputArray.add(outputJson);
            }
            jsonObject.add("output", outputArray);

            jsonObject.addProperty("outputChance", outputChance);
        }

        @Override
        public Identifier getRecipeId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return DnaExtractingRecipe.Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject toAdvancementJson() {
            return null;
        }

        @Nullable
        @Override
        public Identifier getAdvancementId() {
            return null;
        }
    }
}
