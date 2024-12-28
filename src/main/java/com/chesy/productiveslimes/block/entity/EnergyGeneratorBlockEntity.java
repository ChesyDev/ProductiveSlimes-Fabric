package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.handler.CustomEnergyStorage;
import com.chesy.productiveslimes.handler.ItemStackHandler;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.screen.custom.EnergyGeneratorMenu;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyGeneratorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return canBurn(stack);
        }
    };
    protected final PropertyDelegate data;

    private final CustomEnergyStorage energyHandler = new CustomEnergyStorage(10000, 0, 100, 0);

    private final ItemStackHandler upgradeHandler = new ItemStackHandler(4) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() == ModItems.ENERGY_MULTIPLIER_UPGRADE;
        }
    };

    private int progress = 0;
    private int maxProgress = 100;

    public CustomEnergyStorage getEnergyHandler(){
        return energyHandler;
    }

    public PropertyDelegate getData(){
        return data;
    }

    public ItemStackHandler getItemHandler(){
        return itemHandler;
    }

    public ItemStackHandler getUpgradeHandler(){
        return upgradeHandler;
    }

    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_GENERATOR, pos, state);
        this.data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index){
                    case 0 -> EnergyGeneratorBlockEntity.this.energyHandler.getAmountStored();
                    case 1 -> EnergyGeneratorBlockEntity.this.energyHandler.getMaxAmountStored();
                    case 2 -> EnergyGeneratorBlockEntity.this.progress;
                    case 3 -> EnergyGeneratorBlockEntity.this.maxProgress;
                    default -> throw new UnsupportedOperationException("Unexpected value: " + index);
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index){
                    case 0 -> EnergyGeneratorBlockEntity.this.energyHandler.setAmount(value);
                    case 2 -> EnergyGeneratorBlockEntity.this.progress = value;
                    case 3 -> EnergyGeneratorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return null;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new EnergyGeneratorMenu(syncId, playerInventory, this, this.data);
    }

    //Left off

    public int getBurnTime(ItemStack stack) {
        if(stack.getItem() == ModBlocks.ENERGY_SLIME_BLOCK.asItem()){
            return 1000;
        }
        else if (stack.getItem() == ProductiveSlimes.ENERGY_SLIME_BALL) {
            return 100;
        }

        return 0;
    }

    public boolean canBurn(ItemStack stack) {
        return getBurnTime(stack) > 0;
    }
}
