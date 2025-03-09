package com.chesy.productiveslimes.block.item;

import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.util.ImmutableFluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.function.Consumer;

public class TooltipBlockItem extends BlockItem {
    public TooltipBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        if (stack.getOrDefault(ModDataComponents.ENERGY, 0) != 0) {
            int energy = stack.getOrDefault(ModDataComponents.ENERGY, 0);
            textConsumer.accept(Text.translatable("tooltip.productiveslimes.energy_stored")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)))
                    .append(Text.translatable("tooltip.productiveslimes.energy_amount", energy)
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFF)))));
        }

        if (stack.getOrDefault(ModDataComponents.FLUID_VARIANT, FluidVariant.blank()) != FluidVariant.blank()) {
            ImmutableFluidVariant immutableFluidStack = stack.get(ModDataComponents.FLUID_VARIANT);
            FluidVariant fluidStack = (immutableFluidStack != null) ? FluidVariant.of(immutableFluidStack.fluid()) : FluidVariant.blank();
            textConsumer.accept(Text.translatable("tooltip.productiveslimes.fluid_stored").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))).append(Text.translatable(fluidStack.getFluid().getDefaultState().getBlockState().getBlock().getTranslationKey()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFF)))));
            textConsumer.accept(Text.translatable("tooltip.productiveslimes.stored_amount").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))).append(Text.translatable("tooltip.productiveslimes.fluid_amount", immutableFluidStack.amount() / FluidConstants.BUCKET).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFF)))));
        }
    }
}
