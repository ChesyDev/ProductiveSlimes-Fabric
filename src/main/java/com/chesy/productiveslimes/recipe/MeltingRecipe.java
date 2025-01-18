package com.chesy.productiveslimes.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MeltingRecipe implements Recipe<SingleStackRecipeInput> {
    private final DefaultedList<Ingredient> inputItems;
    private final List<ItemStack> output;
    private final int inputCount;
    private final int energy;

    public MeltingRecipe(List<Ingredient> inputItems, List<ItemStack> output, int inputCount, int energy) {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ingredients.addAll(inputItems);
        this.inputItems = ingredients;
        this.output = output;
        this.inputCount = inputCount;
        this.energy = energy;
    }

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        if (world.isClient()){
            return false;
        }

        return inputItems.getFirst().test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.isEmpty() ? ItemStack.EMPTY : output.getFirst().copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleStackRecipeInput>> getSerializer() {
        return (RecipeSerializer<? extends Recipe<SingleStackRecipeInput>>) ModRecipes.MELTING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleStackRecipeInput>> getType() {
        return ModRecipes.MELTING_TYPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forShapeless(inputItems);
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return null;
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

    public static class Serializer implements RecipeSerializer<MeltingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private final MapCodec<MeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.inputItems),
                ItemStack.CODEC.listOf().fieldOf("output").forGetter(recipe -> recipe.output),
                Codec.INT.fieldOf("inputCount").forGetter(recipe -> recipe.inputCount),
                Codec.INT.fieldOf("energy").forGetter(recipe -> recipe.energy)
        ).apply(instance, MeltingRecipe::new));

        public static final PacketCodec<RegistryByteBuf, MeltingRecipe> STREAM_CODEC = PacketCodec.ofStatic(
                MeltingRecipe.Serializer::toNetwork, MeltingRecipe.Serializer::fromNetwork
        );

        private static MeltingRecipe fromNetwork(RegistryByteBuf buffer) {
            int ingredientCount = buffer.readVarInt();
            List<Ingredient> inputItems = new ArrayList<>(ingredientCount);
            for (int i = 0; i < ingredientCount; i++) {
                inputItems.add(Ingredient.PACKET_CODEC.decode(buffer));
            }

            int outputCount = buffer.readVarInt();
            List<ItemStack> result = new ArrayList<>(outputCount);
            for (int i = 0; i < outputCount; i++) {
                result.add(ItemStack.PACKET_CODEC.decode(buffer));
            }

            int inputCount = buffer.readInt();

            int energy = buffer.readInt();

            return new MeltingRecipe(inputItems, result, inputCount, energy);
        }

        private static void toNetwork(RegistryByteBuf buffer, MeltingRecipe recipe) {
            buffer.writeVarInt(recipe.inputItems.size());
            for (Ingredient ingredient : recipe.inputItems) {
                Ingredient.PACKET_CODEC.encode(buffer, ingredient);
            }

            buffer.writeVarInt(recipe.output.size());
            for (ItemStack itemStack : recipe.output) {
                ItemStack.PACKET_CODEC.encode(buffer, itemStack);
            }

            buffer.writeInt(recipe.inputCount);
            buffer.writeInt(recipe.energy);
        }

        @Override
        public MapCodec<MeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MeltingRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
