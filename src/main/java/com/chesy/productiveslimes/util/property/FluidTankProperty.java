package com.chesy.productiveslimes.util.property;

import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

public record FluidTankProperty() implements BooleanProperty {
    public static final MapCodec<FluidTankProperty> MAP_CODEC = MapCodec.unit(new FluidTankProperty());
    @Override
    public boolean getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        return !stack.contains(ModDataComponents.FLUID_VARIANT);
    }

    @Override
    public MapCodec<? extends BooleanProperty> getCodec() {
        return MAP_CODEC;
    }
}
