package com.chesy.productiveslimes.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record ImmutableFluidVariant(Fluid fluid, long amount) {
    public static final Codec<ImmutableFluidVariant> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.LONG.fieldOf("amount").forGetter(variant -> variant.amount),
                    Codec.STRING.fieldOf("fluid").forGetter(variant -> {
                        Identifier registryName = Registries.FLUID.getId(variant.fluid);
                        return registryName.toString();
                    })
            ).apply(instance, (amount, fluid) -> new ImmutableFluidVariant(FluidVariant.of(Registries.FLUID.get(Identifier.of(fluid))).getFluid(), amount))
    );

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
