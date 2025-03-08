package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.block.entity.SlimeNestBlockEntity;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.item.custom.NestUpgradeItem;
import com.chesy.productiveslimes.item.custom.SlimeItem;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.screen.slot.SlotItemHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeNestMenu extends ScreenHandler {
    public final SlimeNestBlockEntity blockEntity;
    private final World level;
    private final PropertyDelegate data;

    public SlimeNestMenu(int pContainerId, PlayerInventory inv, BlockPos blockPos) {
        this(pContainerId, inv, inv.player.getWorld().getBlockEntity(blockPos), new ArrayPropertyDelegate(6));
    }

    public SlimeNestMenu(int pContainerId, PlayerInventory inv, BlockEntity entity, PropertyDelegate data) {
        super(ModMenuTypes.SLIME_NEST_MENU_HANDLER, pContainerId);
        blockEntity = (SlimeNestBlockEntity) entity;
        this.level = inv.player.getWorld();
        this.data = data;

        this.addSlot(new SlotItemHandler(blockEntity, 0, 8, 7, itemStack -> itemStack.getItem() instanceof NestUpgradeItem, 1));
        this.addSlot(new SlotItemHandler(blockEntity, 1, 8, 25, itemStack -> itemStack.getItem() instanceof NestUpgradeItem, 1));
        this.addSlot(new SlotItemHandler(blockEntity, 2, 8, 43, itemStack -> itemStack.getItem() instanceof NestUpgradeItem, 1));
        this.addSlot(new SlotItemHandler(blockEntity, 3, 8, 61, itemStack -> itemStack.getItem() instanceof NestUpgradeItem, 1));

        this.addSlot(new SlotItemHandler(blockEntity, 4, 33, 34, itemStack -> itemStack.getItem() instanceof SlimeItem && itemStack.contains(ModDataComponents.SLIME_DATA), 1));

        this.addSlot(new SlotItemHandler(blockEntity, 5, 116, 16, itemStack -> false));
        this.addSlot(new SlotItemHandler(blockEntity, 6, 134, 16, itemStack -> false));
        this.addSlot(new SlotItemHandler(blockEntity, 7, 152, 16, itemStack -> false));
        this.addSlot(new SlotItemHandler(blockEntity, 8, 116, 34, itemStack -> false));
        this.addSlot(new SlotItemHandler(blockEntity, 9, 134, 34, itemStack -> false));
        this.addSlot(new SlotItemHandler(blockEntity, 10, 152, 34, itemStack -> false));
        this.addSlot(new SlotItemHandler(blockEntity, 11, 116, 52, itemStack -> false));
        this.addSlot(new SlotItemHandler(blockEntity, 12, 134, 52, itemStack -> false));
        this.addSlot(new SlotItemHandler(blockEntity, 13, 152, 52, itemStack -> false));

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addProperties(data);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.blockEntity.size()) {
                if (!this.insertItem(originalStack, this.blockEntity.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.blockEntity.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return this.blockEntity.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public int getCountdown() {
        return (data.get(1) - data.get(0)) / 20;
    }

    public boolean hasSlime() {
        return data.get(1) > 0;
    }

    public boolean hasOutputSlot() {
        return data.get(2) == 1;
    }

    public int getSlimeSize() {
        return data.get(3);
    }

    public int getCooldown() {
        return data.get(1);
    }

    public float getMultiplier() {
        return data.get(5) / 1000f;
    }

    public ItemStack getDrop() {
        if (blockEntity.getItems().get(blockEntity.slimeSlot[0]).isEmpty()) {
            return ItemStack.EMPTY;
        }
        return blockEntity.getSlime().get(ModDataComponents.SLIME_DATA).dropItem();
    }
}
