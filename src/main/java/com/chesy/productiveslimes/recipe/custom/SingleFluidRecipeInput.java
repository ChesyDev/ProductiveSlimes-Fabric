package com.chesy.productiveslimes.recipe.custom;

import com.chesy.productiveslimes.fluid.FluidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record SingleFluidRecipeInput(FluidStack fluidStack) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        throw new UnsupportedOperationException("Fluid recipe input does not have an item.");
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return fluidStack.isEmpty();
    }
}