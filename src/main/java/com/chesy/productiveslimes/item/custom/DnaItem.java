package com.chesy.productiveslimes.item.custom;

import net.minecraft.item.Item;

public class DnaItem extends Item {
    public final int color;

    public DnaItem(int pColor, Settings settings) {
        super(settings);
        this.color = pColor;
    }

    public int getColor() {
        return color;
    }
}
