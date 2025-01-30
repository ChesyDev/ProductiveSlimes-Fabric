package com.chesy.productiveslimes.util.property;

import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.util.ImmutableFluidVariant;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

public record FluidTankProperty15k() implements BooleanProperty {
    public static final MapCodec<FluidTankProperty15k> MAP_CODEC = MapCodec.unit(new FluidTankProperty15k());
    @Override
    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        ImmutableFluidVariant immutableFluidVariant = stack.getOrDefault(ModDataComponents.FLUID_VARIANT, null);
        long amount = immutableFluidVariant == null ? 0 : immutableFluidVariant.amount();
        return amount <= FluidConstants.BUCKET * 15;
    }

    @Override
    public MapCodec<? extends BooleanProperty> getCodec() {
        return MAP_CODEC;
    }
}
