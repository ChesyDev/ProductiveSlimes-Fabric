package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.handler.CustomEnergyStorage;
import com.chesy.productiveslimes.recipe.MeltingRecipe;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.recipe.SolidingRecipe;
import com.chesy.productiveslimes.screen.custom.MeltingStationMenu;
import com.chesy.productiveslimes.screen.custom.SolidingStationMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SolidingStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    private final CustomEnergyStorage energyHandler = new CustomEnergyStorage(10000, 1000, 0, 0){
        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };

    protected final PropertyDelegate data;
    private int progress = 0;
    private int maxProgress = 78;

    private final int OUTPUT_SLOT = 1;

    public SolidingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLIDING_STATION, pos, state);
        this.data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SolidingStationBlockEntity.this.progress;
                    case 1 -> SolidingStationBlockEntity.this.maxProgress;
                    case 2 -> SolidingStationBlockEntity.this.energyHandler.getAmountStored();
                    case 3 -> SolidingStationBlockEntity.this.energyHandler.getMaxAmountStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SolidingStationBlockEntity.this.progress = value;
                    case 1 -> SolidingStationBlockEntity.this.maxProgress = value;
                    case 2 -> SolidingStationBlockEntity.this.energyHandler.setAmount(value);
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    public ItemStack getInputHandler() {
        return this.inventory.get(0);
    }

    public ItemStack getBucketHandler() {
        return this.inventory.get(1);
    }

    public ItemStack getOutputHandler() {
        return this.inventory.get(2);
    }

    public int outputHandlerCount(){
        return 1;
    }

    public CustomEnergyStorage getEnergyHandler() {
        return energyHandler;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.productiveslimes.soliding_station");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SolidingStationMenu(syncId, playerInventory, this, this.data);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        Inventories.writeNbt(nbt, inventory, registries);
        nbt.putInt("EnergyInventory", energyHandler.getAmountStored());

        nbt.putInt("soliding_station.progress", progress);

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        Inventories.readNbt(nbt, inventory, registries);
        energyHandler.setAmount(nbt.getInt("EnergyInventory"));

        progress = nbt.getInt("soliding_station.progress");

        super.readNbt(nbt, registries);
    }

    public void tick(World pWorld, BlockPos pPos, BlockState pState) {
        Optional<RecipeEntry<SolidingRecipe>> recipe = getCurrentRecipe();

        if(hasRecipe() && getBucketHandler().getCount() >= recipe.get().value().getOutputs().get(0).getCount() && energyHandler.getAmountStored() >= recipe.get().value().getEnergy()){
            increaseCraftingProgress();
            markDirty(pWorld, pPos, pState);

            if(hasProgressFinished()) {
                energyHandler.removeAmount(recipe.get().value().getEnergy());
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        Optional<RecipeEntry<SolidingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            List<ItemStack> results = recipe.get().value().getOutputs();

            // Extract the input item from the input slot
            getInputHandler().decrement(recipe.get().value().getInputCount());

            // Loop through each result item and find suitable output slots
            for (ItemStack result : results) {
                int outputSlot = findSuitableOutputSlot(result);
                if (outputSlot != -1) {
                    this.inventory.set(OUTPUT_SLOT, new ItemStack(result.getItem(),
                            getOutputHandler().getCount() + result.getCount()));
                } else {
                    // Handle the case where no suitable output slot is found
                    // This can be logging an error, throwing an exception, or any other handling logic
                    System.err.println("No suitable output slot found for item: " + result);
                }
            }
        }
    }

    private int findSuitableOutputSlot(ItemStack result) {
        // Implement logic to find a suitable output slot for the given result
        // Return the slot index or -1 if no suitable slot is found
        for (int i = 0; i < outputHandlerCount(); i++) {
            ItemStack stackInSlot = getOutputHandler();
            if (stackInSlot.isEmpty() || (stackInSlot.getItem() == result.getItem() && stackInSlot.getCount() + result.getCount() <= stackInSlot.getMaxCount())) {
                return i;
            }
        }
        return -1;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<SolidingRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        if (getInputHandler().getCount() < recipe.get().value().getInputCount()) {
            return false;
        }

        List<ItemStack> results = recipe.get().value().getOutputs();

        for (ItemStack result : results) {
            if (!canInsertAmountIntoOutputSlot(result) || !canInsertItemIntoOutputSlot(result.getItem())) {
                return false;
            }
        }

        return checkSlot(results);
    }

    private boolean checkSlot(List<ItemStack> results){
        int count = 0;
        int emptyCount = 0;
        for (ItemStack result : results){
            count++;
        }

        for (int i = 0; i < outputHandlerCount(); i++) {
            ItemStack stackInSlot = getOutputHandler();
            if(!stackInSlot.isEmpty()){
                for (ItemStack result : results){
                    if(stackInSlot.getItem() == result.getItem()){
                        if(stackInSlot.getCount() + result.getCount() <= 64){
                            emptyCount++;
                        }
                    }
                }
            }
            else {
                emptyCount++;
            }
        }

        return emptyCount >= count;
    }

    private Optional<RecipeEntry<SolidingRecipe>> getCurrentRecipe(){
        ServerWorld world = (ServerWorld) this.world;
        return world.getRecipeManager().getFirstMatch(ModRecipes.SOLIDING_TYPE, new SingleStackRecipeInput(getInputHandler()), world);
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        for (int i = 0; i < outputHandlerCount(); i++) {
            ItemStack stackInSlot = getOutputHandler();
            if (stackInSlot.isEmpty() || (stackInSlot.getItem() == result.getItem() && stackInSlot.getCount() + result.getCount() <= stackInSlot.getMaxCount())) {
                return true;
            }
        }
        return false;
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        for (int i = 0; i < outputHandlerCount(); i++) {
            ItemStack stackInSlot = getOutputHandler();
            if (stackInSlot.isEmpty() || stackInSlot.getItem() == item) {
                return true;
            }
        }
        return false;
    }


    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    public PropertyDelegate getData() {
        return data;
    }
}
