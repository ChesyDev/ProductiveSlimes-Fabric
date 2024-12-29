package com.chesy.productiveslimes.handler;

import com.chesy.productiveslimes.handler.items.IItemHandler;
import com.chesy.productiveslimes.handler.items.IItemHandlerModifiable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SlotItemHandler extends Slot {
    private static Inventory emptyInventory = new SimpleInventory(0);
    private final IItemHandler itemHandler;
    protected final int index;

    public SlotItemHandler(IItemHandler itemHandler, int index, int x, int y) {
        super(emptyInventory, index, x, y);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if(stack.isEmpty()){
            return false;
        }
        return itemHandler.isItemValid(index, stack);
    }

    @Override
    public ItemStack getStack() {
        return this.getItemHandler().getStackInSlot(index);
    }

    @Override
    public void setStack(ItemStack stack) {
        ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(index, stack);
        this.markDirty();
    }

    public void initialize(ItemStack stack) {
        ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(index, stack);
        this.markDirty();
    }

    @Override
    public void onQuickTransfer(ItemStack newItem, ItemStack original) {}

    @Override
    public int getMaxItemCount() {
        return this.itemHandler.getSlotLimit(this.index);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxCount();
        maxAdd.setCount(maxInput);

        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(index);
        if (handler instanceof ItemStackHandler) {
            IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable) handler;

            handlerModifiable.setStackInSlot(index, ItemStack.EMPTY);

            ItemStack remainder = handlerModifiable.insertItem(index, maxAdd, true);

            handlerModifiable.setStackInSlot(index, currentStack);

            return maxInput - remainder.getCount();
        } else {
            ItemStack remainder = handler.insertItem(index, maxAdd, true);

            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;
        }
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return !this.getItemHandler().extractItem(index, 1, true).isEmpty();
    }

    @Override
    public ItemStack takeStack(int amount) {
        return this.getItemHandler().extractItem(index, amount, false);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }
}
