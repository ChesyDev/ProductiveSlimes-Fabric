package com.chesy.productiveslimes.screen.slot;

import com.chesy.productiveslimes.block.entity.MeltingStationBlockEntity;
import com.chesy.productiveslimes.block.entity.SolidingStationBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SolidingStationInputSlot extends Slot {
    public final SolidingStationBlockEntity blockEntity;
    public SolidingStationInputSlot(Inventory inventory, int index, int x, int y, SolidingStationBlockEntity blockEntity) {
        super(inventory, index, x, y);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return true;
    }
}
