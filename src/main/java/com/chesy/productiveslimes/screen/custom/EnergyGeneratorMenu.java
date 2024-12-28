package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.entity.EnergyGeneratorBlockEntity;
import com.chesy.productiveslimes.handler.ItemStackHandler;
import com.chesy.productiveslimes.handler.SlotItemHandler;
import com.chesy.productiveslimes.screen.ModMenuTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyGeneratorMenu extends ScreenHandler {
    public final EnergyGeneratorBlockEntity blockEntity;
    private final World world;
    private final PropertyDelegate data;
    private boolean showExtraSlots = true;
    private final PlayerInventory playerInventory;

    public EnergyGeneratorMenu(int syncId, PlayerInventory inv, BlockPos pos, EnergyGeneratorBlockEntity entity, PropertyDelegate data) {
        this(syncId, inv, entity, data);
    }

    public EnergyGeneratorMenu(int syncId, PlayerInventory inv, EnergyGeneratorBlockEntity entity, PropertyDelegate data) {
        super(ModMenuTypes.ENERGY_GENERATOR_MENU_HANDLER, syncId);
        checkSize(inv, 4);
        this.blockEntity = entity;
        this.world = entity.getWorld();
        this.data = data;
        this.playerInventory = inv;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        ItemStackHandler iItemHandler = blockEntity.getItemHandler();
        this.addSlot(new SlotItemHandler(iItemHandler, 0, 80, 25));

        ItemStackHandler upgradeHandler = blockEntity.getUpgradeHandler();
        this.addSlot(new SlotItemHandler(upgradeHandler, 0, 179, 29));
        this.addSlot(new SlotItemHandler(upgradeHandler, 1, 197, 29));
        this.addSlot(new SlotItemHandler(upgradeHandler, 2, 179, 47));
        this.addSlot(new SlotItemHandler(upgradeHandler, 3, 197, 47));

        addProperties(data);
    }

    public void toggleExtraSlots() {
        this.showExtraSlots = !this.showExtraSlots;
        this.slots.clear();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        ItemStackHandler iItemHandler = blockEntity.getItemHandler();
        this.addSlot(new SlotItemHandler(iItemHandler, 0, 80, 25));

        if (showExtraSlots) {
            ItemStackHandler upgradeHandler = blockEntity.getUpgradeHandler();
            this.addSlot(new SlotItemHandler(upgradeHandler, 0, 179, 29));
            this.addSlot(new SlotItemHandler(upgradeHandler, 1, 197, 29));
            this.addSlot(new SlotItemHandler(upgradeHandler, 2, 179, 47));
            this.addSlot(new SlotItemHandler(upgradeHandler, 3, 197, 47));
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
    public ItemStack quickMove(PlayerEntity player, int slot) {
        Slot sourceSlot = slots.get(slot);
        if (sourceSlot == null || !sourceSlot.hasStack()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (slot < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!insertItem(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (slot < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!insertItem(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + slot);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.setStack(ItemStack.EMPTY);
        } else {

        }
        sourceSlot.onTakeItem(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean isValid(int slot) {
        return isValid(slot);
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
        return true;
    }
}
