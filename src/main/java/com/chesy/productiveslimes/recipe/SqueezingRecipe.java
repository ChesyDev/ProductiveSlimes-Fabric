package com.chesy.productiveslimes.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public record SqueezingRecipe(List<Ingredient> inputItems, List<ItemStack> output, int energy) implements Recipe<SingleStackRecipeInput> {

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        if (world.isClient){
            return false;
        }
        return inputItems.getFirst().test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.isEmpty() ? ItemStack.EMPTY : output.getFirst().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output.isEmpty() ? ItemStack.EMPTY : output.getFirst().copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleStackRecipeInput>> getSerializer() {
        return ModRecipes.SQUEEZING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleStackRecipeInput>> getType() {
        return ModRecipes.SQUEEZING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<SqueezingRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<SqueezingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").forGetter(SqueezingRecipe::inputItems),
                        ItemStack.CODEC.listOf().fieldOf("output").forGetter(SqueezingRecipe::output),
                        Codec.INT.fieldOf("energy").forGetter(SqueezingRecipe::energy)
                ).apply(instance, SqueezingRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, SqueezingRecipe> PACKET_CODEC = PacketCodec.of(
                (value, buf) -> {
                    buf.writeVarInt(value.inputItems().size());
                    for (Ingredient ingredient : value.inputItems()) {
                        Ingredient.PACKET_CODEC.encode(buf, ingredient);
                    }

                    buf.writeVarInt(value.output().size());
                    for (ItemStack itemStack : value.output()) {
                        ItemStack.PACKET_CODEC.encode(buf, itemStack);
                    }

                    buf.writeInt(value.energy());
                },
                buf -> {
                    int inputItemsSize = buf.readVarInt();
                    List<Ingredient> inputItems = new ArrayList<>(inputItemsSize);
                    for (int i = 0; i < inputItemsSize; i++) {
                        inputItems.add(Ingredient.PACKET_CODEC.decode(buf));
                    }

                    int outputSize = buf.readVarInt();
                    List<ItemStack> output = new ArrayList<>(outputSize);
                    for (int i = 0; i < outputSize; i++) {
                        output.add(ItemStack.PACKET_CODEC.decode(buf));
                    }

                    int energy = buf.readInt();

                    return new SqueezingRecipe(inputItems, output, energy);
                }
        );

        @Override
        public MapCodec<SqueezingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SqueezingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
