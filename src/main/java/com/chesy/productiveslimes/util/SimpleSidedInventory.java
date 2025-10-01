package com.chesy.productiveslimes.util;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.Direction;

public abstract class SimpleSidedInventory extends SimpleInventory implements SidedInventory {
    public SimpleSidedInventory(int size){
        super(size);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] result = new int[size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }

        return result;
    }
}