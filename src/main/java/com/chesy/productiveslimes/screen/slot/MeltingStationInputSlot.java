package com.chesy.productiveslimes.screen.slot;

import com.chesy.productiveslimes.block.entity.MeltingStationBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class MeltingStationInputSlot extends Slot {
    public final MeltingStationBlockEntity blockEntity;
    public MeltingStationInputSlot(Inventory inventory, int index, int x, int y, MeltingStationBlockEntity blockEntity) {
        super(inventory, index, x, y);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return true;
    }
}
