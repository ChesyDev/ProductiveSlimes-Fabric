package com.chesy.productiveslimes.recipe;

import com.chesy.productiveslimes.util.FluidStack;
import com.chesy.productiveslimes.util.SizedIngredient;
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
import net.minecraft.world.World;

public record MeltingRecipe(SizedIngredient inputItems, FluidStack output, int energy) implements Recipe<SingleStackRecipeInput> {
    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        if (world.isClient()){
            return false;
        }

        return inputItems.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.isEmpty() ? ItemStack.EMPTY : output.getFluid().getFluid().getBucketItem().getDefaultStack();
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleStackRecipeInput>> getSerializer() {
        return ModRecipes.MELTING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleStackRecipeInput>> getType() {
        return ModRecipes.MELTING_TYPE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return ModRecipes.MELTING_CATEGORY;
    }

    public static class Serializer implements RecipeSerializer<MeltingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<MeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SizedIngredient.CODEC.fieldOf("ingredients").forGetter(recipe -> recipe.inputItems),
                FluidStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                Codec.INT.fieldOf("energy").forGetter(recipe -> recipe.energy)
        ).apply(instance, MeltingRecipe::new));

        public static final PacketCodec<RegistryByteBuf, MeltingRecipe> STREAM_CODEC = PacketCodec.ofStatic(
                MeltingRecipe.Serializer::toNetwork, MeltingRecipe.Serializer::fromNetwork
        );

        private static MeltingRecipe fromNetwork(RegistryByteBuf buffer) {
            SizedIngredient inputItems = SizedIngredient.PACKET_CODEC.decode(buffer);

            FluidStack result = FluidStack.STREAM_CODEC.decode(buffer);

            int energy = buffer.readInt();

            return new MeltingRecipe(inputItems, result, energy);
        }

        private static void toNetwork(RegistryByteBuf buffer, MeltingRecipe recipe) {
            SizedIngredient.PACKET_CODEC.encode(buffer, recipe.inputItems);

            FluidStack.STREAM_CODEC.encode(buffer, recipe.output);

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
