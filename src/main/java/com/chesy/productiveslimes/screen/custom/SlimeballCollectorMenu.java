package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.block.entity.SlimeballCollectorBlockEntity;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.util.SlotItemHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeballCollectorMenu extends ScreenHandler {
    public final SlimeballCollectorBlockEntity inventory;
    private final World level;
    private final PropertyDelegate data;

    public SlimeballCollectorMenu(int pContainerId, PlayerInventory inv, BlockPos blockPos) {
        this(pContainerId, inv, inv.player.getWorld().getBlockEntity(blockPos), new ArrayPropertyDelegate(2));
    }

    public SlimeballCollectorMenu(int pContainerId, PlayerInventory inv, BlockEntity entity, PropertyDelegate data) {
        super(ModMenuTypes.SLIMEBALL_COLLECTOR_MENU_HANDLER, pContainerId);
        inventory = (SlimeballCollectorBlockEntity) entity;
        this.level = inv.player.getWorld();
        this.data = data;

        for (int i = 0; i < inventory.size(); i++) {
            addSlot(new SlotItemHandler(inventory, i, 8 + (i * 18), 34, itemStack -> false));
        }

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
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
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
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
