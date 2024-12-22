package com.chesy.productiveslimes.item.custom;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;

public class BucketItem extends net.minecraft.item.BucketItem {
    public final int color;
    private final Fluid fluid;
    private final int amount = 1000;

    public BucketItem(Fluid pFluid, Settings settings, int pColor) {
        super(pFluid, settings);
        this.color = pColor;
        this.fluid = pFluid;
    }

    public int getColor() {
        return color;
    }

    public FluidVariant getFluidStack() {
        return FluidVariant.of(fluid);
    }

    public int getAmount() {
        return amount;
    }
}
