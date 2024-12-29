package com.chesy.productiveslimes.handler;

import com.chesy.productiveslimes.common.util.INBTSerializable;
import com.chesy.productiveslimes.handler.items.IItemHandler;
import com.chesy.productiveslimes.handler.items.IItemHandlerModifiable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.collection.DefaultedList;

import javax.swing.plaf.basic.BasicComboBoxUI;

public class ItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NbtCompound> {
    protected DefaultedList<ItemStack> stacks;

    public ItemStackHandler(){
        this(1);
    }

    public ItemStackHandler(int size) {
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    public ItemStackHandler(DefaultedList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public void setSize(int size) {
        stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemStack.areItemsAndComponentsEqual(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
            } else {
                existing.increment(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxCount());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return existing.copyWithCount(toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return Item.DEFAULT_MAX_COUNT;
    }

    protected int getStackLimit(int slot, ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxCount());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }

    @Override
    public NbtCompound serializeNBT(RegistryWrapper.WrapperLookup registryWrapper) {
        NbtList nbtTagList = new NbtList();
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (!stacks.get(i).isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                nbtTagList.add(stack.toNbt(registryWrapper, itemTag));
            }
        }
        NbtCompound nbt = new NbtCompound();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(RegistryWrapper.WrapperLookup registryWrapper, NbtCompound nbt) {
        setSize(nbt.contains("Size", NbtElement.INT_TYPE) ? nbt.getInt("Size") : stacks.size());
        NbtList tagList = nbt.getList("Items", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < tagList.size(); i++) {
            NbtCompound itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size()) {
                ItemStack.CODEC.parse(registryWrapper.getOps(NbtOps.INSTANCE), itemTags).result().ifPresent(stack -> stacks.set(slot, stack));
            }
        }
        onLoad();
    }

    protected void onLoad() {}

    protected void onContentsChanged(int slot) {}
}
