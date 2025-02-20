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
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public record DnaSynthesizingRecipe(List<Ingredient> inputItems, List<ItemStack> output, int energy, int inputCount, Identifier id) implements Recipe<SimpleInventory> {
    @Override
    public boolean matches(SimpleInventory input, World world) {
        List<ItemStack> inputItems = input.stacks;

        List<Ingredient> remainingIngredients = new ArrayList<>(this.inputItems);

        for (ItemStack itemStack : inputItems) {
            if (itemStack.isEmpty()) {
                continue;
            }

            boolean ingredientFound = false;
            Iterator<Ingredient> iterator = remainingIngredients.iterator();

            while (iterator.hasNext()) {
                Ingredient ingredient = iterator.next();
                if (ingredient.test(itemStack)) {
                    iterator.remove();
                    ingredientFound = true;
                    break;
                }
            }

            if (!ingredientFound) {
                return false;
            }
        }

        return remainingIngredients.isEmpty();
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
        return ModRecipes.DNA_SYNTHESIZING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SimpleInventory>> getType() {
        return ModRecipes.DNA_SYNTHESIZING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<DnaSynthesizingRecipe>{
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public DnaSynthesizingRecipe read(Identifier id, JsonObject json) {
            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            List<Ingredient> inputItems = new ArrayList<>(ingredients.size());

            for (int i = 0; i < ingredients.size(); i++){
                inputItems.add(Ingredient.fromJson(ingredients.get(i)));
            }

            List<ItemStack> output = new ArrayList<>();
            JsonArray outputArray = JsonHelper.getArray(json, "output");

            for(JsonElement element : outputArray){
                output.add(ShapedRecipe.outputFromJson(element.getAsJsonObject()));
            }

            int energy = JsonHelper.getInt(json, "energy");
            int inputCount = JsonHelper.getInt(json, "inputCount");

            return new DnaSynthesizingRecipe(inputItems, output, energy, inputCount, id);
        }

        @Override
        public DnaSynthesizingRecipe read(Identifier id, PacketByteBuf buf) {
            int inputItemsSize = buf.readVarInt();
            List<Ingredient> inputItems = new ArrayList<>(inputItemsSize);
            for (int i = 0; i < inputItemsSize; i++){
                inputItems.add(Ingredient.fromPacket(buf));
            }

            int outputSize = buf.readVarInt();
            List<ItemStack> output = new ArrayList<>(outputSize);
            for (int i = 0; i < outputSize; i++){
                output.add(buf.readItemStack());
            }

            int energy = buf.readVarInt();

            int inputCount = buf.readVarInt();

            return new DnaSynthesizingRecipe(inputItems, output, energy, inputCount, id);
        }

        @Override
        public void write(PacketByteBuf buf, DnaSynthesizingRecipe value) {
            buf.writeVarInt(value.inputItems().size());
            for (Ingredient ingredient : value.inputItems()){
                ingredient.write(buf);
            }

            buf.writeVarInt(value.output().size());
            for (ItemStack itemStack : value.output()){
                buf.writeItemStack(itemStack);
            }

            buf.writeVarInt(value.energy());
            buf.writeVarInt(value.inputCount());
        }
    }
}
