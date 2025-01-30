package com.chesy.productiveslimes.item.custom;

import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;

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
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.literal("Increases the speed of the slime nest by " + String.format("%.2f", multiplier) + "x").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));

    }
}