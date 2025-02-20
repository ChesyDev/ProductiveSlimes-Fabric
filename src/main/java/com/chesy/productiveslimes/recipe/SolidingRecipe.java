package com.chesy.productiveslimes.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SolidingRecipe implements Recipe<SimpleInventory> {
    private final DefaultedList<Ingredient> inputItems;
    private final List<ItemStack> output;
    private final int inputCount;
    private final int energy;
    private final Identifier id;

    public SolidingRecipe(List<Ingredient> inputItems, List<ItemStack> output, int inputCount, int energy, Identifier id) {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ingredients.addAll(inputItems);
        this.inputItems = ingredients;
        this.output = output;
        this.inputCount = inputCount;
        this.energy = energy;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleInventory input, World world) {
        if (world.isClient()) {
            return false;
        }

        return inputItems.get(0).test(input.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.isEmpty() ? ItemStack.EMPTY : output.get(0).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.isEmpty() ? ItemStack.EMPTY : output.get(0).copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SimpleInventory>> getSerializer() {
        return ModRecipes.SOLIDING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SimpleInventory>> getType() {
        return ModRecipes.SOLIDING_TYPE;
    }

    public List<ItemStack> getOutputs() {
        return output;
    }

    public int getInputCount() {
        return inputCount;
    }

    public DefaultedList<Ingredient> getInputItems() {
        return inputItems;
    }

    public int getEnergy() {
        return energy;
    }

    public static class Serializer implements RecipeSerializer<SolidingRecipe> {
        public static final SolidingRecipe.Serializer INSTANCE = new SolidingRecipe.Serializer();

        @Override
        public SolidingRecipe read(Identifier id, JsonObject jsonObject) {
            JsonArray ingredients = JsonHelper.getArray(jsonObject, "ingredients");
            List<Ingredient> inputItems = new ArrayList<>();

            for (int i = 0; i < ingredients.size(); i++) {
                inputItems.add(Ingredient.fromJson(ingredients.get(i)));
            }

            JsonArray outputs = JsonHelper.getArray(jsonObject, "output");
            List<ItemStack> output = new ArrayList<>();

            for(JsonElement element : outputs) {
                output.add(ShapedRecipe.outputFromJson(element.getAsJsonObject()));
            }

            int inputCount = JsonHelper.getInt(jsonObject, "inputCount");
            int energy = JsonHelper.getInt(jsonObject, "energy");

            return new SolidingRecipe(inputItems, output, inputCount, energy, id);
        }

        @Override
        public SolidingRecipe read(Identifier id, PacketByteBuf buffer) {
            int ingredientCount = buffer.readVarInt();
            List<Ingredient> inputItems = new ArrayList<>(ingredientCount);
            for (int i = 0; i < ingredientCount; i++) {
                inputItems.add(Ingredient.fromPacket(buffer));
            }

            int outputCount = buffer.readVarInt();
            List<ItemStack> result = new ArrayList<>(outputCount);
            for (int i = 0; i < outputCount; i++) {
                result.add(buffer.readItemStack());
            }

            int inputCount = buffer.readInt();

            int energy = buffer.readInt();

            return new SolidingRecipe(inputItems, result, inputCount, energy, id);
        }

        @Override
        public void write(PacketByteBuf buffer, SolidingRecipe recipe) {
            buffer.writeVarInt(recipe.inputItems.size());
            for (Ingredient ingredient : recipe.inputItems) {
                ingredient.write(buffer);
            }

            buffer.writeVarInt(recipe.output.size());
            for (ItemStack itemStack : recipe.output) {
                buffer.writeItemStack(itemStack);
            }

            buffer.writeInt(recipe.inputCount);
            buffer.writeInt(recipe.energy);
        }
    }
}
