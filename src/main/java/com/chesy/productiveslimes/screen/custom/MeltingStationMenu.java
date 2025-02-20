package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.block.entity.MeltingStationBlockEntity;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import com.chesy.productiveslimes.util.SlotItemHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class MeltingStationMenu extends ScreenHandler {
    public final MeltingStationBlockEntity blockEntity;
    private final PropertyDelegate data;
    private final Inventory inventory;

    public MeltingStationMenu(int syncId, PlayerInventory inv, PacketByteBuf byteBuf) {
        this(syncId, inv, inv.player.getWorld().getBlockEntity(byteBuf.readBlockPos()),new ArrayPropertyDelegate(4));
    }

    public MeltingStationMenu(int syncId, PlayerInventory inv, BlockEntity entity, PropertyDelegate data) {
        super(ModMenuTypes.MELTING_STATION_MENU_HANDLER, syncId);
        this.blockEntity = ((MeltingStationBlockEntity) entity);
        this.data = data;
        this.inventory = (Inventory) entity;

        this.addSlot(new SlotItemHandler(inventory, 0, 25, 34, itemStack -> itemStack.getItem() == Items.BUCKET));
        this.addSlot(new SlotItemHandler(inventory, 1, 45, 34, itemStack -> true));
        this.addSlot(new SlotItemHandler(inventory, 2, 134, 34, itemStack -> false));

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addProperties(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 26; // This is the height in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
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

    public int getEnergy() {
        return this.data.get(2);
    }

    public int getMaxEnergy() {
        return this.data.get(3);
    }

    public int getEnergyStoredScaled() {
        return (int) (((float) getEnergy() / (float) getMaxEnergy()) * 57);
    }
}
