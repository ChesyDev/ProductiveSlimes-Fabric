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
import java.util.List;

public record SqueezingRecipe(List<Ingredient> inputItems, List<ItemStack> output, int energy, Identifier id) implements Recipe<SimpleInventory> {
    @Override
    public boolean matches(SimpleInventory input, World world) {
        if (world.isClient){
            return false;
        }
        return inputItems.getFirst().test(input.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.isEmpty() ? ItemStack.EMPTY : output.getFirst().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.isEmpty() ? ItemStack.EMPTY : output.getFirst().copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SimpleInventory>> getSerializer() {
        return ModRecipes.SQUEEZING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SimpleInventory>> getType() {
        return ModRecipes.SQUEEZING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<SqueezingRecipe>{
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public SqueezingRecipe read(Identifier id, JsonObject jsonObject) {
            JsonArray ingredients = JsonHelper.getArray(jsonObject, "ingredients");
            List<Ingredient> inputItems = new ArrayList<>();

            for (int i = 0; i < ingredients.size(); i++) {
                inputItems.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            JsonArray outputs = JsonHelper.getArray(jsonObject, "output");
            List<ItemStack> output = new ArrayList<>();

            for(JsonElement element : outputs) {
                output.add(ShapedRecipe.outputFromJson(element.getAsJsonObject()));
            }

            int energy = JsonHelper.getInt(jsonObject, "energy");

            return new SqueezingRecipe(inputItems, output, energy, id);
        }

        @Override
        public SqueezingRecipe read(Identifier id, PacketByteBuf buf) {
            int inputItemsSize = buf.readVarInt();
            List<Ingredient> inputItems = new ArrayList<>(inputItemsSize);
            for (int i = 0; i < inputItemsSize; i++) {
                inputItems.add(Ingredient.fromPacket(buf));
            }

            int outputSize = buf.readVarInt();
            List<ItemStack> output = new ArrayList<>(outputSize);
            for (int i = 0; i < outputSize; i++) {
                output.add(buf.readItemStack());
            }

            int energy = buf.readInt();

            return new SqueezingRecipe(inputItems, output, energy, id);
        }

        @Override
        public void write(PacketByteBuf buf, SqueezingRecipe value) {
            buf.writeVarInt(value.inputItems().size());
            for (Ingredient ingredient : value.inputItems()) {
                ingredient.write(buf);
            }

            buf.writeVarInt(value.output().size());
            for (ItemStack itemStack : value.output()) {
                buf.writeItemStack(itemStack);
            }

            buf.writeInt(value.energy());
        }
    }
}
