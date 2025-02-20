package com.chesy.productiveslimes.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyMultiplierUpgrade extends Item {

    public EnergyMultiplierUpgrade(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canBeNested() {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(
                Text.translatable("tooltip.productiveslimes.energy_multiplier_upgrade_desc").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));

        tooltip.add(Text.translatable(""));

        tooltip.add(Text.translatable("tooltip.productiveslimes.stack_count").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN))
                .append(Text.literal("1 / 2 / 3 / 4").setStyle(Style.EMPTY.withColor(Formatting.GRAY))));

        tooltip.add(Text.translatable("tooltip.productiveslimes.multiplier").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN))
                .append(Text.literal("x5 / x10 / x20 / x40").setStyle(Style.EMPTY.withColor(Formatting.GRAY))));
    }
}
