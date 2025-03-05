package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.recipe.DnaSynthesizingRecipe;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.recipe.custom.MultipleRecipeInput;
import com.chesy.productiveslimes.screen.custom.DnaSynthesizerMenu;
import com.chesy.productiveslimes.util.*;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
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
import team.reborn.energy.api.EnergyStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DnaSynthesizerBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos>, IEnergyBlockEntity {
    private float rotation;
    private final CustomEnergyStorage energyHandler = new CustomEnergyStorage(10000, 1000, 0,0){
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
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    protected final PropertyDelegate data;
    private int progress = 0;
    private int maxProgress = 120;
    private int[] inputSlots = new int[]{0, 1, 2};
    private int[] eggSlots = new int[]{3};
    private int[] outputSlots = new int[]{4};

    public DnaSynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DNA_SYNTHESIZER, pos, state);
        this.data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> DnaSynthesizerBlockEntity.this.progress;
                    case 1 -> DnaSynthesizerBlockEntity.this.maxProgress;
                    case 2 -> DnaSynthesizerBlockEntity.this.energyHandler.getAmountStored();
                    case 3 -> DnaSynthesizerBlockEntity.this.energyHandler.getMaxAmountStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> DnaSynthesizerBlockEntity.this.progress = value;
                    case 1 -> DnaSynthesizerBlockEntity.this.maxProgress = value;
                    case 2 -> DnaSynthesizerBlockEntity.this.energyHandler.setAmount(value);
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
        return pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.productiveslimes.dna_synthesizer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new DnaSynthesizerMenu(syncId, playerInventory, this, data);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, inventory, registries);
        nbt.putInt("energy", energyHandler.getAmountStored());
        nbt.putInt("progress", progress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, inventory, registries);
        energyHandler.setAmount(nbt.getInt("energy"));
        progress = nbt.getInt("progress");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return side == Direction.DOWN && Arrays.stream(outputSlots).anyMatch(value -> value == slot);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return (side != Direction.DOWN && side != Direction.UP && Arrays.stream(inputSlots).anyMatch(value -> value == slot)) || (side == Direction.UP && Arrays.stream(eggSlots).anyMatch(value -> value == slot));
    }

    public void tick(World pLevel, BlockPos pPos, BlockState pState) {
        Optional<RecipeEntry<DnaSynthesizingRecipe>> recipe = getCurrentRecipe();

        if(hasRecipe() && energyHandler.getAmountStored() >= recipe.get().value().energy() && !inventory.get(eggSlots[0]).isEmpty()){
            increaseCraftingProgress();
            markDirty(pLevel, pPos, pState);

            if(hasProgressFinished()) {
                energyHandler.removeAmount(recipe.get().value().energy());
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
        Optional<RecipeEntry<DnaSynthesizingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            List<SizedIngredient> ingredients = recipe.get().value().inputItems();
            List<ItemStack> results = recipe.get().value().output();

            // Extract the input item from the input slot
            int i  = 0;
            for (SizedIngredient ingredient : ingredients) {
                this.removeStack(inputSlots[i], ingredient.count());
                i++;
            }
            this.removeStack(eggSlots[0], 1);

            // Loop through each result item and find suitable output slots
            for (ItemStack result : results) {
                int outputSlot = findSuitableOutputSlot(result);
                if (outputSlot != -1) {
                    this.setStack(outputSlot, new ItemStack(result.getItem(),
                            this.inventory.get(outputSlot).getCount() + result.getCount()));

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
        for (int i : outputSlots) {
            ItemStack stackInSlot = this.inventory.get(i);
            if (stackInSlot.isEmpty() || (stackInSlot.getItem() == result.getItem() && stackInSlot.getCount() + result.getCount() <= stackInSlot.getMaxCount())) {
                return i;
            }
        }
        return -1;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<DnaSynthesizingRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        List<ItemStack> results = recipe.get().value().output();

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

        for (int i : outputSlots) {
            ItemStack stackInSlot = this.inventory.get(i);
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

    private Optional<RecipeEntry<DnaSynthesizingRecipe>> getCurrentRecipe(){
        MultipleRecipeInput input = new MultipleRecipeInput(List.of(inventory.get(inputSlots[0]), inventory.get(inputSlots[1]), inventory.get(inputSlots[2])));
        ServerWorld level = (ServerWorld) this.world;
        return level.getRecipeManager().getFirstMatch(ModRecipes.DNA_SYNTHESIZING_TYPE, input, level);
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        for (int i : outputSlots) {
            ItemStack stackInSlot = this.inventory.get(i);
            if (stackInSlot.isEmpty() || (stackInSlot.getItem() == result.getItem() && stackInSlot.getCount() + result.getCount() <= stackInSlot.getMaxCount())) {
                return true;
            }
        }
        return false;
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        for (int i : outputSlots) {
            ItemStack stackInSlot = this.inventory.get(i);
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

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        ContainerUtils.dropContents(world, pos, this);
        super.onBlockReplaced(pos, oldState);
    }
}
