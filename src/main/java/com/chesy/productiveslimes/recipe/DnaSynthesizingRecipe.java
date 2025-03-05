package com.chesy.productiveslimes.recipe;

import com.chesy.productiveslimes.recipe.custom.MultipleRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public record DnaSynthesizingRecipe(List<Ingredient> inputItems, List<ItemStack> output, int energy, int inputCount) implements Recipe<MultipleRecipeInput> {
    @Override
    public boolean matches(MultipleRecipeInput input, World world) {
        List<ItemStack> inputItems = input.inputItems();
        if (inputItems.size() != this.inputItems.size()) {
            return false;
        }

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
    public ItemStack craft(MultipleRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.isEmpty() ? ItemStack.EMPTY : output.getFirst().copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<MultipleRecipeInput>> getSerializer() {
        return ModRecipes.DNA_SYNTHESIZING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<MultipleRecipeInput>> getType() {
        return ModRecipes.DNA_SYNTHESIZING_TYPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return ModRecipes.DNA_SYNTHESIZING_CATEGORY;
    }

    public static class Serializer implements RecipeSerializer<DnaSynthesizingRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<DnaSynthesizingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(DnaSynthesizingRecipe::inputItems),
                        ItemStack.CODEC.listOf().fieldOf("output").forGetter(DnaSynthesizingRecipe::output),
                        Codec.INT.fieldOf("energy").forGetter(DnaSynthesizingRecipe::energy),
                        Codec.INT.fieldOf("inputCount").forGetter(DnaSynthesizingRecipe::inputCount)
                ).apply(instance, DnaSynthesizingRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, DnaSynthesizingRecipe> PACKET_CODEC = PacketCodec.of(
                (value, buf) -> {
                    buf.writeVarInt(value.inputItems().size());
                    for (Ingredient ingredient : value.inputItems()){
                        Ingredient.PACKET_CODEC.encode(buf, ingredient);
                    }

                    buf.writeVarInt(value.output().size());
                    for (ItemStack itemStack : value.output()){
                        ItemStack.PACKET_CODEC.encode(buf, itemStack);
                    }

                    buf.writeVarInt(value.energy());
                    buf.writeVarInt(value.inputCount());
                },
                buf -> {
                    int inputItemsSize = buf.readVarInt();
                    List<Ingredient> inputItems = new ArrayList<>(inputItemsSize);
                    for (int i = 0; i < inputItemsSize; i++){
                        inputItems.add(Ingredient.PACKET_CODEC.decode(buf));
                    }

                    int outputSize = buf.readVarInt();
                    List<ItemStack> output = new ArrayList<>(outputSize);
                    for (int i = 0; i < outputSize; i++){
                        output.add(ItemStack.PACKET_CODEC.decode(buf));
                    }

                    int energy = buf.readVarInt();

                    int inputCount = buf.readVarInt();

                    return new DnaSynthesizingRecipe(inputItems, output, energy, inputCount);
                }
        );

        @Override
        public MapCodec<DnaSynthesizingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, DnaSynthesizingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
