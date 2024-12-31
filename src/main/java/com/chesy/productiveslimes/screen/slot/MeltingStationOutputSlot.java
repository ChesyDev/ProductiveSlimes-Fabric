package com.chesy.productiveslimes.screen.slot;

import com.chesy.productiveslimes.item.ModItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class MeltingStationOutputSlot extends Slot {
    public MeltingStationOutputSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxItemCount() {
        return 64;
    }
}
