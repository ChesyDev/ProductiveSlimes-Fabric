package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.block.entity.EnergyGeneratorBlockEntity;
import com.chesy.productiveslimes.item.ModItems;
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

public class EnergyGeneratorMenu extends ScreenHandler {
    public final EnergyGeneratorBlockEntity blockEntity;
    private final PropertyDelegate data;
    private final Inventory inventory;
    private final PlayerInventory playerInventory;
    private boolean showExtraSlots = true;

    public EnergyGeneratorMenu(int syncId, PlayerInventory inv, BlockPos blockPos) {
        this(syncId, inv, inv.player.getWorld().getBlockEntity(blockPos),new ArrayPropertyDelegate(4));
    }

    public EnergyGeneratorMenu(int syncId, PlayerInventory playerInventory, BlockEntity entity, PropertyDelegate data) {
        super(ModMenuTypes.ENERGY_GENERATOR_MENU_HANDLER, syncId);
        this.blockEntity = ((EnergyGeneratorBlockEntity) entity);
        this.data = data;
        this.inventory = (Inventory) entity;
        this.playerInventory = playerInventory;

        this.addSlot(new SlotItemHandler(inventory, 0, 80, 25, blockEntity::canBurn));
        this.addSlot(new SlotItemHandler(inventory, 1, 179, 29, this::isEnergyMultiplierUpgrade, 1));
        this.addSlot(new SlotItemHandler(inventory, 2, 197, 29, this::isEnergyMultiplierUpgrade, 1));
        this.addSlot(new SlotItemHandler(inventory, 3, 179, 47, this::isEnergyMultiplierUpgrade, 1));
        this.addSlot(new SlotItemHandler(inventory, 4, 197, 47, this::isEnergyMultiplierUpgrade, 1));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(data);
    }

    public void toggleExtraSlots() {
        this.showExtraSlots = !this.showExtraSlots;
        this.slots.clear();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addSlot(new SlotItemHandler(inventory, 0, 80, 25, blockEntity::canBurn));

        if (showExtraSlots) {
            addSlot(new SlotItemHandler(inventory, 1, 179, 29, this::isEnergyMultiplierUpgrade, 1));
            addSlot(new SlotItemHandler(inventory, 2, 197, 29, this::isEnergyMultiplierUpgrade, 1));
            addSlot(new SlotItemHandler(inventory, 3, 179, 47, this::isEnergyMultiplierUpgrade, 1));
            addSlot(new SlotItemHandler(inventory, 4, 197, 47, this::isEnergyMultiplierUpgrade, 1));
        }

        this.sendContentUpdates();
    }

    public boolean isCrafting() {
        return data.get(2) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(2);
        int maxProgress = this.data.get(3);  // Max Progress
        int progressArrowSize = 14; // This is the height in pixels of your arrow

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

    public EnergyGeneratorBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public int getEnergy() {
        return this.data.get(0);
    }

    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getBurnTime() {
        return this.data.get(2);
    }

    public int getMaxBurnTime() {
        return this.data.get(3);
    }

    public int getEnergyStoredScaled() {
        return (int) (((float) getEnergy() / (float) getMaxEnergy()) * 57);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private boolean isEnergyMultiplierUpgrade(ItemStack stack) {
        return stack.getItem() == ModItems.ENERGY_MULTIPLIER_UPGRADE;
    }
}
