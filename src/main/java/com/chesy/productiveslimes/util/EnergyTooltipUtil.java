package com.chesy.productiveslimes.util;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public record EnergyTooltipUtil(int mouseX1, int mouseX2, int mouseY1, int mouseY2, int energyRequired, int maxCapacity) {
    public Text getText(){
        return Text.translatable("gui.productiveslimes.energy_stored", energyRequired, maxCapacity);
    }
}
