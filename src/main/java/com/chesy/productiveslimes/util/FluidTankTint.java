package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public record FluidTankTint(int defaultColor) implements TintSource {
    public static final MapCodec<FluidTankTint> MAP_CODEC = RecordCodecBuilder.mapCodec(fluidTankTintInstance ->
            fluidTankTintInstance.group(
                    Codecs.ARGB.fieldOf("default").forGetter(FluidTankTint::defaultColor)
            ).apply(fluidTankTintInstance, FluidTankTint::new)
    );

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        if (stack.contains(ModDataComponents.FLUID_VARIANT)){
            ImmutableFluidVariant immutableFluidVariant = stack.get(ModDataComponents.FLUID_VARIANT);
            FluidVariant fluidVariant = FluidVariant.of(immutableFluidVariant.fluid());
            return FluidVariantRendering.getColor(fluidVariant);
        }

        return defaultColor;
    }

    @Override
    public MapCodec<? extends TintSource> getCodec() {
        return MAP_CODEC;
    }
}
