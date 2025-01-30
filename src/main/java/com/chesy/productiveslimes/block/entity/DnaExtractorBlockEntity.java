package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.recipe.DnaExtractingRecipe;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.screen.custom.DnaExtractorMenu;
import com.chesy.productiveslimes.util.ContainerUtils;
import com.chesy.productiveslimes.util.CustomEnergyStorage;
import com.chesy.productiveslimes.util.IEnergyBlockEntity;
import com.chesy.productiveslimes.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class DnaExtractorBlockEntity extends BlockEntity implements ImplementedInventory, IEnergyBlockEntity, ExtendedScreenHandlerFactory<BlockPos> {
    private float rotation;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private final int[] inputSlots = new int[]{0};
    private final int[] outputSlots = new int[]{1, 2};
    private final CustomEnergyStorage energyHandler = new CustomEnergyStorage(10000, 1000, 0, 0);
    protected final PropertyDelegate data;
    private int progress = 0;
    private int maxProgress = 78;

    public DnaExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DNA_EXTRACTOR, pos, state);
        this.data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> DnaExtractorBlockEntity.this.progress;
                    case 1 -> DnaExtractorBlockEntity.this.maxProgress;
                    case 2 -> DnaExtractorBlockEntity.this.energyHandler.getAmountStored();
                    case 3 -> DnaExtractorBlockEntity.this.energyHandler.getMaxAmountStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> DnaExtractorBlockEntity.this.progress = value;
                    case 1 -> DnaExtractorBlockEntity.this.maxProgress = value;
                    case 2 -> DnaExtractorBlockEntity.this.energyHandler.setAmount(value);
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public CustomEnergyStorage getEnergyHandler() {
        return energyHandler;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.productiveslimes.dna_extractor");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new DnaExtractorMenu(syncId, playerInventory, this, data);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        Inventories.writeNbt(nbt, inventory, registries);
        nbt.put("Energy", energyHandler.serializeNBT(registries));
        nbt.putInt("Progress", progress);
        nbt.putInt("MaxProgress", maxProgress);

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        Inventories.readNbt(nbt, inventory, registries);
        energyHandler.deserializeNBT(registries, nbt.getCompound("Energy"));
        progress = nbt.getInt("Progress");
        maxProgress = nbt.getInt("MaxProgress");
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
        return side != Direction.DOWN && Arrays.stream(inputSlots).anyMatch(value -> value == slot);
    }

    public void tick(World pLevel, BlockPos pPos, BlockState pState) {
        Optional<RecipeEntry<DnaExtractingRecipe>> recipe = getCurrentRecipe();
        if(hasRecipe() && energyHandler.getAmountStored() >= recipe.get().value().energy()){
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
        Optional<RecipeEntry<DnaExtractingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            List<ItemStack> results = recipe.get().value().output();

            // Extract the input item from the input slot
            this.removeStack(inputSlots[0], recipe.get().value().inputCount());

            // Loop through each result item and find suitable output slots
            for (ItemStack result : results) {
                int outputSlot = findSuitableOutputSlot(result);
                if (outputSlot != -1) {
                    if (result.getItem() == Items.SLIME_BALL){
                        this.setStack(outputSlot, new ItemStack(result.getItem(),
                                this.inventory.get(outputSlot).getCount() + result.getCount()));
                    }
                    else{
                        Random random = new Random();
                        float chance = recipe.get().value().outputChance();
                        if (random.nextFloat() < chance){
                            this.setStack(outputSlot, new ItemStack(result.getItem(),
                                    this.inventory.get(outputSlot).getCount() + result.getCount()));
                        }
                    }

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
        Optional<RecipeEntry<DnaExtractingRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        if (inventory.get(0).getCount() < recipe.get().value().inputCount()) {
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

    private Optional<RecipeEntry<DnaExtractingRecipe>> getCurrentRecipe(){
        ServerWorld level = (ServerWorld) this.world;
        return level.getRecipeManager().getFirstMatch(ModRecipes.DNA_EXTRACTING_TYPE, new SingleStackRecipeInput(inventory.get(0)), level);
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

    public ItemStack getRenderStack() {
        if (inventory.get(outputSlots[0]).isEmpty() && inventory.get(outputSlots[1]).isEmpty()) {
            return inventory.get(inputSlots[0]);
        }
        else {
            if (!inventory.get(outputSlots[0]).isEmpty() && inventory.get(outputSlots[0]).getItem() != Items.SLIME_BALL) {
                return inventory.get(outputSlots[0]);
            }
            else {
                if (inventory.get(outputSlots[1]).isEmpty()){
                    return inventory.get(outputSlots[0]);
                }
                else {
                    return inventory.get(outputSlots[1]);

                }
            }
        }
    }

    public float getRenderingRotation() {
        rotation += 1f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    @Override
    public void onStateReplaced(BlockPos pos, BlockState oldState) {
        ContainerUtils.dropContents(world, pos, this);
        super.onStateReplaced(pos, oldState);
    }
}
