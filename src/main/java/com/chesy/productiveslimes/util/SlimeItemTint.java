package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

public record SlimeItemTint(int defaultColor) implements TintSource {
    public static final MapCodec<SlimeItemTint> MAP_CODEC = RecordCodecBuilder.mapCodec(slimeItemTintInstance ->
            slimeItemTintInstance.group(
                    Codecs.ARGB.fieldOf("default").forGetter(SlimeItemTint::defaultColor)
            ).apply(slimeItemTintInstance, SlimeItemTint::new)
    );

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        if (stack.contains(ModDataComponents.SLIME_DATA)) {
            return ColorHelper.fullAlpha(stack.get(ModDataComponents.SLIME_DATA).color());
        }
        return defaultColor;
    }

    @Override
    public MapCodec<? extends TintSource> getCodec() {
        return MAP_CODEC;
    }
}
