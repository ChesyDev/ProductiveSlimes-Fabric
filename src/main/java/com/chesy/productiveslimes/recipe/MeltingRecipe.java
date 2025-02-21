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
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MeltingRecipe implements Recipe<SimpleInventory> {
    private final DefaultedList<Ingredient> inputItems;
    private final List<ItemStack> output;
    private final int inputCount;
    private final int energy;
    private final Identifier id;

    public MeltingRecipe(List<Ingredient> inputItems, List<ItemStack> output, int inputCount, int energy, Identifier id) {
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
        if (world.isClient()){
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
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<SimpleInventory>> getType() {
        return Type.INSTANCE;
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

    public static class Type implements RecipeType<MeltingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "melting";
    }

    public static class Serializer implements RecipeSerializer<MeltingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "melting";

        @Override
        public MeltingRecipe read(Identifier id, JsonObject json) {
            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            List<Ingredient> inputItems = new ArrayList<>();

            for (int i = 0; i < ingredients.size(); i++) {
                inputItems.add(Ingredient.fromJson(ingredients.get(i)));
            }

            JsonArray outputs = JsonHelper.getArray(json, "output");
            List<ItemStack> output = new ArrayList<>();

            for(JsonElement element : outputs) {
                output.add(ShapedRecipe.outputFromJson(element.getAsJsonObject()));
            }

            int inputCount = JsonHelper.getInt(json, "inputCount");
            int energy = JsonHelper.getInt(json, "energy");

            return new MeltingRecipe(inputItems, output, inputCount, energy, id);
        }

        @Override
        public MeltingRecipe read(Identifier id, PacketByteBuf buffer) {
            int ingredientCount = buffer.readInt();
            List<Ingredient> inputItems = new ArrayList<>(ingredientCount);
            for (int i = 0; i < ingredientCount; i++) {
                inputItems.add(Ingredient.fromPacket(buffer));
            }

            int outputCount = buffer.readInt();
            List<ItemStack> result = new ArrayList<>(outputCount);
            for (int i = 0; i < outputCount; i++) {
                result.add(buffer.readItemStack());
            }

            int inputCount = buffer.readInt();

            int energy = buffer.readInt();

            return new MeltingRecipe(inputItems, result, inputCount, energy, id);
        }

        @Override
        public void write(PacketByteBuf buffer, MeltingRecipe recipe) {
            buffer.writeInt(recipe.inputItems.size());
            for (Ingredient ingredient : recipe.inputItems) {
                ingredient.write(buffer);
            }

            buffer.writeInt(recipe.output.size());
            for (ItemStack itemStack : recipe.output) {
                buffer.writeItemStack(itemStack);
            }

            buffer.writeInt(recipe.inputCount);
            buffer.writeInt(recipe.energy);
        }
    }
}
