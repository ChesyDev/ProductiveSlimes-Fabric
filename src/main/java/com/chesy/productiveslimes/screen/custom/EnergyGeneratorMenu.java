package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.block.entity.EnergyGeneratorBlockEntity;
import com.chesy.productiveslimes.screen.slot.EnergyGeneratorInputSlot;
import com.chesy.productiveslimes.screen.slot.EnergyGeneratorUpgradeSlot;
import com.chesy.productiveslimes.screen.ModMenuTypes;
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

public class EnergyGeneratorMenu extends ScreenHandler {
    public EnergyGeneratorBlockEntity blockEntity;
    private World world;
    private PropertyDelegate data;
    private boolean showExtraSlots = true;
    private PlayerInventory playerInventory;
    private final Inventory inventory;

    public EnergyGeneratorMenu(int syncId, PlayerInventory inv, BlockPos blockPos) {
        this(syncId, inv, inv.player.getWorld().getBlockEntity(blockPos),new ArrayPropertyDelegate(4));
    }

    public EnergyGeneratorMenu(int syncId, PlayerInventory inv, BlockEntity entity, PropertyDelegate data) {
        super(ModMenuTypes.ENERGY_GENERATOR_MENU_HANDLER, syncId);
        this.blockEntity = ((EnergyGeneratorBlockEntity) entity);
        this.data = data;
        this.playerInventory = inv;
        this.inventory = (Inventory) entity;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new EnergyGeneratorInputSlot(inventory, 0, 80, 25, blockEntity));
        this.addSlot(new EnergyGeneratorUpgradeSlot(inventory, 1, 179, 29));
        this.addSlot(new EnergyGeneratorUpgradeSlot(inventory, 2, 197, 29));
        this.addSlot(new EnergyGeneratorUpgradeSlot(inventory, 3, 179, 47));
        this.addSlot(new EnergyGeneratorUpgradeSlot(inventory, 4, 197, 47));

        addProperties(data);
    }

    public void toggleExtraSlots() {
        this.showExtraSlots = !this.showExtraSlots;
        this.slots.clear();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addSlot(new EnergyGeneratorInputSlot(inventory, 0, 80, 25, blockEntity));

        if (showExtraSlots) {
            addSlot(new EnergyGeneratorUpgradeSlot(inventory, 1, 179, 29));
            addSlot(new EnergyGeneratorUpgradeSlot(inventory, 2, 197, 29));
            addSlot(new EnergyGeneratorUpgradeSlot(inventory, 3, 179, 47));
            addSlot(new EnergyGeneratorUpgradeSlot(inventory, 4, 197, 47));
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

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 5;  // must be the number of slots you have!

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
}
