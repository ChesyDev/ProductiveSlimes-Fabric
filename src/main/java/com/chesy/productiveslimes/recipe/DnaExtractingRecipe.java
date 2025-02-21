package com.chesy.productiveslimes.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public record DnaExtractingRecipe(List<Ingredient> inputItems, List<ItemStack> output, int inputCount, int energy, float outputChance, Identifier id) implements Recipe<SimpleInventory> {
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

    public static class Type implements RecipeType<DnaExtractingRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "dna_extracting";
    }

    public static class Serializer implements RecipeSerializer<DnaExtractingRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "dna_extracting";

        @Override
        public DnaExtractingRecipe read(Identifier id, JsonObject json) {
            JsonArray ingredient = JsonHelper.getArray(json, "ingredients");
            List<Ingredient> inputItems = new ArrayList<>(ingredient.size());

            for (int i = 0; i < ingredient.size(); i++) {
                inputItems.add(Ingredient.fromJson(ingredient.get(i)));
            }

            JsonArray output = JsonHelper.getArray(json, "output");
            List<ItemStack> outputItems = new ArrayList<>(output.size());

            for (JsonElement element : output){
                outputItems.add(ShapedRecipe.outputFromJson(element.getAsJsonObject()));
            }

            int inputCount = JsonHelper.getInt(json, "inputCount");
            int energy = JsonHelper.getInt(json, "energy");
            float outputChance = JsonHelper.getFloat(json, "outputChance");

            return new DnaExtractingRecipe(inputItems, outputItems, inputCount, energy, outputChance, id);
        }

        @Override
        public DnaExtractingRecipe read(Identifier id, PacketByteBuf buf) {
            int inputItemsSize = buf.readInt();
            List<Ingredient> inputItems = new ArrayList<>();
            for (int i = 0; i < inputItemsSize; i++) {
                inputItems.add(Ingredient.fromPacket(buf));
            }

            int outputSize = buf.readInt();
            List<ItemStack> output = new ArrayList<>(outputSize);
            for (int i = 0; i < outputSize; i++) {
                output.add(buf.readItemStack());
            }

            int inputCount = buf.readInt();
            int energy = buf.readInt();
            float outputChance = buf.readFloat();

            return new DnaExtractingRecipe(inputItems, output, inputCount, energy, outputChance, id);
        }

        @Override
        public void write(PacketByteBuf buf, DnaExtractingRecipe value) {
            buf.writeInt(value.inputItems().size());
            for (Ingredient ingredient : value.inputItems()) {
                ingredient.write(buf);
            }

            buf.writeInt(value.output().size());
            for (ItemStack itemStack : value.output()) {
                buf.writeItemStack(itemStack);
            }

            buf.writeInt(value.inputCount());
            buf.writeInt(value.energy());
            buf.writeFloat(value.outputChance());
        }
    }
}
