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

public class EnergyMultiplierUpgrade extends Item{

    public EnergyMultiplierUpgrade(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canBeNested() {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(
                Text.translatable("tooltip.productiveslimes.energy_multiplier_upgrade_desc").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));

        textConsumer.accept(Text.translatable(""));

        textConsumer.accept(Text.translatable("tooltip.productiveslimes.stack_count").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN))
                .append(Text.literal("1 / 2 / 3 / 4").setStyle(Style.EMPTY.withColor(Formatting.GRAY))));

        textConsumer.accept(Text.translatable("tooltip.productiveslimes.multiplier").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN))
                .append(Text.literal("x5 / x10 / x20 / x40").setStyle(Style.EMPTY.withColor(Formatting.GRAY))));
    }
}
