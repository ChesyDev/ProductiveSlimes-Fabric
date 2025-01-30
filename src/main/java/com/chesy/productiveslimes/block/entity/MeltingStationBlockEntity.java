package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.util.ContainerUtils;
import com.chesy.productiveslimes.util.CustomEnergyStorage;
import com.chesy.productiveslimes.recipe.MeltingRecipe;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.screen.custom.MeltingStationMenu;
import com.chesy.productiveslimes.util.IEnergyBlockEntity;
import com.chesy.productiveslimes.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MeltingStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory, IEnergyBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    private final CustomEnergyStorage energyHandler = new CustomEnergyStorage(10000, 1000, 0, 0){
        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            markDirty();
            if (world != null){
                world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            }
        }

        @Override
        public boolean supportsInsertion() {
            return true;
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }
    };

    protected final PropertyDelegate data;
    private int progress = 0;
    private int maxProgress = 78;

    private final int BUCKET_SLOT = 0;
    private final int INPUT_SLOT = 1;
    private final int OUTPUT_SLOT = 2;

    public MeltingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MELTING_STATION, pos, state);
        this.data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MeltingStationBlockEntity.this.progress;
                    case 1 -> MeltingStationBlockEntity.this.maxProgress;
                    case 2 -> MeltingStationBlockEntity.this.energyHandler.getAmountStored();
                    case 3 -> MeltingStationBlockEntity.this.energyHandler.getMaxAmountStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MeltingStationBlockEntity.this.progress = value;
                    case 1 -> MeltingStationBlockEntity.this.maxProgress = value;
                    case 2 -> MeltingStationBlockEntity.this.energyHandler.setAmount(value);
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
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
        return Text.translatable("block.productiveslimes.melting_station");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MeltingStationMenu(syncId, playerInventory, this, this.data);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        Inventories.writeNbt(nbt, inventory, registries);
        nbt.putInt("EnergyInventory", energyHandler.getAmountStored());

        nbt.putInt("melting_station.progress", progress);

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        Inventories.readNbt(nbt, inventory, registries);
        energyHandler.setAmount(nbt.getInt("EnergyInventory"));

        progress = nbt.getInt("melting_station.progress");
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        if (slot == BUCKET_SLOT) {
            return stack.getItem().equals(Items.BUCKET);
        }

        return slot == INPUT_SLOT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return slot == OUTPUT_SLOT;
    }

    public void tick(World pWorld, BlockPos pPos, BlockState pState) {
        Optional<RecipeEntry<MeltingRecipe>> recipe = getCurrentRecipe();

        if(hasRecipe() && this.getStack(BUCKET_SLOT).getCount() >= recipe.get().value().getOutputs().get(0).getCount() && energyHandler.getAmountStored() >= recipe.get().value().getEnergy()){
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
        Optional<RecipeEntry<MeltingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            List<ItemStack> results = recipe.get().value().getOutputs();

            this.removeStack(INPUT_SLOT, recipe.get().value().getInputCount());

            for (ItemStack result : results) {
                int outputSlot = findSuitableOutputSlot(result);
                if (outputSlot != -1) {
                    this.inventory.set(OUTPUT_SLOT, new ItemStack(result.getItem(), this.getStack(OUTPUT_SLOT).getCount() + result.getCount()));
                } else {
                    System.err.println("No suitable output slot found for item: " + result);
                }
            }
        }
    }

    private int findSuitableOutputSlot(ItemStack result) {
        ItemStack stackInSlot = this.getStack(OUTPUT_SLOT);
        if (stackInSlot.isEmpty() || (stackInSlot.getItem() == result.getItem() && stackInSlot.getCount() + result.getCount() <= stackInSlot.getMaxCount())) {
            return OUTPUT_SLOT;
        }

        return -1;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<MeltingRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        if (this.getStack(INPUT_SLOT).getCount() < recipe.get().value().getInputCount()) {
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

        ItemStack stackInSlot = this.getStack(OUTPUT_SLOT);
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

        return emptyCount >= count;
    }

    private Optional<RecipeEntry<MeltingRecipe>> getCurrentRecipe(){
        ServerWorld world = (ServerWorld) this.world;
        return world.getRecipeManager().getFirstMatch(ModRecipes.MELTING_TYPE, new SingleStackRecipeInput(this.getStack(INPUT_SLOT)), world);
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        ItemStack stackInSlot = this.getStack(OUTPUT_SLOT);
        return stackInSlot.isEmpty() || (stackInSlot.getItem() == result.getItem() && stackInSlot.getCount() + result.getCount() <= stackInSlot.getMaxCount());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        ItemStack stackInSlot = this.getStack(OUTPUT_SLOT);
        return stackInSlot.isEmpty() || stackInSlot.getItem() == item;
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

    @Override
    public void onStateReplaced(BlockPos pos, BlockState oldState) {
        ContainerUtils.dropContents(world, pos, this);
        super.onStateReplaced(pos, oldState);
    }
}
