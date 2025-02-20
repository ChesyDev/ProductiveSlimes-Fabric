package com.chesy.productiveslimes.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NestUpgradeItem extends Item {
    private final float multiplier;

    public NestUpgradeItem(Settings pProperties, float multiplier) {
        super(pProperties);
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("tooltip.productiveslimes.nest_upgrade", String.format("%.2f", multiplier)));
    }
}