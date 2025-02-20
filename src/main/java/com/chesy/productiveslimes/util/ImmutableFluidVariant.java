package com.chesy.productiveslimes.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record ImmutableFluidVariant(Fluid fluid, long amount) {
    public NbtCompound toNbt(NbtCompound nbt) {
        nbt.putString("fluid", Registries.FLUID.getId(fluid).toString());
        nbt.putLong("amount", amount);
        return nbt;
    }

    public static ImmutableFluidVariant fromNbt(NbtCompound nbt) {
        return new ImmutableFluidVariant(FluidVariant.of(Registries.FLUID.get(new Identifier(nbt.getString("fluid")))).getFluid(), nbt.getLong("amount"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableFluidVariant that = (ImmutableFluidVariant) o;
        return amount == that.amount && Objects.equals(fluid, that.fluid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluid, amount);
    }
}
