package com.chesy.productiveslimes.screen.slot;

import com.chesy.productiveslimes.block.entity.EnergyGeneratorBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class EnergyGeneratorInputSlot extends Slot {
    public final EnergyGeneratorBlockEntity blockEntity;
    public EnergyGeneratorInputSlot(Inventory inventory, int index, int x, int y, EnergyGeneratorBlockEntity blockEntity) {
        super(inventory, index, x, y);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return blockEntity.canBurn(stack);
    }
}
