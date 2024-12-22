package com.chesy.productiveslimes.item.custom;

import net.minecraft.item.Item;

public class SlimeballItem extends Item {
    public final int color;
    public SlimeballItem(int pColor, Item.Settings pSettings) {
        super(pSettings);
        this.color = pColor;
    }

    public int getColor(){
        return color;
    }
}
