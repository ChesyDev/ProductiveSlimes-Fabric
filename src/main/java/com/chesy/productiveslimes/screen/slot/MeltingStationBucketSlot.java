package com.chesy.productiveslimes.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class MeltingStationBucketSlot extends Slot {
    public MeltingStationBucketSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() == Items.BUCKET;
    }

    @Override
    public int getMaxItemCount() {
        return 64;
    }
}
