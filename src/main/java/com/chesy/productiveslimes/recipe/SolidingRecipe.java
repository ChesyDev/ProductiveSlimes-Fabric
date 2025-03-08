package com.chesy.productiveslimes.recipe;

import com.chesy.productiveslimes.recipe.custom.SingleFluidRecipeInput;
import com.chesy.productiveslimes.fluid.FluidStack;
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
import java.util.List;

public record SolidingRecipe(FluidStack fluidStack, List<ItemStack> output, int energy) implements Recipe<SingleFluidRecipeInput> {
    @Override
    public boolean matches(SingleFluidRecipeInput input, World world) {
        if (world.isClient()){
            return false;
        }

        return input.fluidStack().is(fluidStack.getFluid().getFluid()) && input.fluidStack().getAmount() >= fluidStack.getAmount();
    }

    @Override
    public ItemStack craft(SingleFluidRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.isEmpty() ? ItemStack.EMPTY : output.get(0).copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleFluidRecipeInput>> getSerializer() {
        return ModRecipes.SOLIDING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleFluidRecipeInput>> getType() {
        return ModRecipes.SOLIDING_TYPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return ModRecipes.SOLIDING_CATEGORY;
    }

    public static class Serializer implements RecipeSerializer<SolidingRecipe> {
        public static final SolidingRecipe.Serializer INSTANCE = new SolidingRecipe.Serializer();
        public static final MapCodec<SolidingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidStack.CODEC.fieldOf("ingredients").forGetter(recipe -> recipe.fluidStack),
                ItemStack.CODEC.listOf().fieldOf("output").forGetter(recipe -> recipe.output),
                Codec.INT.fieldOf("energy").forGetter(recipe -> recipe.energy)
        ).apply(instance, SolidingRecipe::new));

        public static final PacketCodec<RegistryByteBuf, SolidingRecipe> STREAM_CODEC = PacketCodec.ofStatic(
                SolidingRecipe.Serializer::toNetwork, SolidingRecipe.Serializer::fromNetwork
        );

        private static SolidingRecipe fromNetwork(RegistryByteBuf buffer) {
            FluidStack inputItems = FluidStack.STREAM_CODEC.decode(buffer);

            int outputCount = buffer.readVarInt();
            List<ItemStack> result = new ArrayList<>(outputCount);
            for (int i = 0; i < outputCount; i++) {
                result.add(ItemStack.PACKET_CODEC.decode(buffer));
            }

            int energy = buffer.readInt();

            return new SolidingRecipe(inputItems, result, energy);
        }

        private static void toNetwork(RegistryByteBuf buffer, SolidingRecipe recipe) {
            FluidStack.STREAM_CODEC.encode(buffer, recipe.fluidStack);

            buffer.writeVarInt(recipe.output.size());
            for (ItemStack itemStack : recipe.output) {
                ItemStack.PACKET_CODEC.encode(buffer, itemStack);
            }

            buffer.writeInt(recipe.energy);
        }

        @Override
        public MapCodec<SolidingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SolidingRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
