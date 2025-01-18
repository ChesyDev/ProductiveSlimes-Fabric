package com.chesy.productiveslimes.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Predicate;

public class SlotItemHandler extends Slot {
    private final Predicate<ItemStack> validator;
    private final int stackLimit;

    public SlotItemHandler(Inventory inventory, int index, int x, int y, Predicate<ItemStack> validator) {
        this(inventory, index, x, y, validator, 64);
    }

    public SlotItemHandler(Inventory inventory, int index, int x, int y, Predicate<ItemStack> validator, int stackLimit) {
        super(inventory, index, x, y);
        this.validator = validator;
        this.stackLimit = stackLimit;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.validator.test(stack);
    }

    @Override
    public int getMaxItemCount() {
        return stackLimit;
    }
}
