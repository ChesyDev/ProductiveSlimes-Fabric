package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.datacomponent.custom.ImmutableFluidVariant;
import com.chesy.productiveslimes.item.custom.BucketItem;
import com.chesy.productiveslimes.recipe.custom.SingleFluidRecipeInput;
import com.chesy.productiveslimes.util.*;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.recipe.SolidingRecipe;
import com.chesy.productiveslimes.screen.custom.SolidingStationMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

import java.util.List;
import java.util.Optional;

public class SolidingStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory, IEnergyBlockEntity, IFluidBlockEntity {
    private final CustomEnergyStorage energyHandler = new CustomEnergyStorage(10000, 1000, 0, 0){
        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            markDirty();
            if (world != null){
                world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            }
        }
    };

    private final SingleFluidStorage fluidTank = new SingleFluidStorage() {
        @Override
        protected long getCapacity(FluidVariant fluidVariant) {
            return FluidConstants.BUCKET * 16;
        }

        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            markDirty();
            if(world != null)
                world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    };
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    public static final int FILL_INPUT_SLOT = 0;
    public static final int FILL_OUTPUT_SLOT = 1;
    public static final int DRAIN_INPUT_SLOT = 2;
    public static final int DRAIN_OUTPUT_SLOT = 3;
    public static final int[] OUTPUT_SLOT = {4};

    protected final PropertyDelegate data;
    private int progress = 0;
    private int maxProgress = 78;

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

    @Override
    public CustomEnergyStorage getEnergyHandler() {
        return energyHandler;
    }

    public SingleFluidStorage getFluidTank() {
        return fluidTank;
    }

    @Override
    public SingleVariantStorage<FluidVariant> getFluidHandler() {
        return fluidTank;
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
        this.fluidTank.writeNbt(nbt, registries);
        nbt.putInt("soliding_station.progress", progress);

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        Inventories.readNbt(nbt, inventory, registries);
        energyHandler.setAmount(nbt.getInt("EnergyInventory"));
        this.fluidTank.readNbt(nbt, registries);
        progress = nbt.getInt("soliding_station.progress");
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == FILL_INPUT_SLOT || slot == DRAIN_INPUT_SLOT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return slot == FILL_OUTPUT_SLOT || slot == DRAIN_OUTPUT_SLOT || slot == OUTPUT_SLOT[0];
    }

    public void tick(World pWorld, BlockPos pPos, BlockState pState) {
        if(pWorld.isClient) {
            return;
        }

        if(this.getStack(FILL_INPUT_SLOT).getItem() instanceof BucketItem bucketItem && fluidTank.getAmount() < fluidTank.getCapacity()){
            if(bucketItem != Items.BUCKET){
                FluidStack fluidStack = new FluidStack(bucketItem.getFluidStack().getFluid(), FluidConstants.BUCKET);

                try(Transaction transaction = Transaction.openOuter()){
                    if (fluidTank.insert(fluidStack.getFluid(), fluidStack.getAmount(), transaction) != 0 && (this.getStack(FILL_OUTPUT_SLOT).getItem() == Items.BUCKET || this.getStack(FILL_OUTPUT_SLOT).isEmpty()) && this.getStack(FILL_OUTPUT_SLOT).getCount() < Items.BUCKET.getMaxCount()){
                        transaction.commit();
                        this.removeStack(FILL_INPUT_SLOT, 1);
                        ItemStack result = new ItemStack(Items.BUCKET);
                        this.inventory.set(FILL_OUTPUT_SLOT, new ItemStack(result.getItem(), this.getStack(FILL_OUTPUT_SLOT).getCount() + result.getCount()));
                    }
                    else{
                        transaction.abort();
                    }
                }
            }
        }
        else if(this.getStack(FILL_INPUT_SLOT).getItem() == ModBlocks.FLUID_TANK.asItem()){
            ItemStack stack = this.getStack(FILL_INPUT_SLOT);

            if (stack.contains(ModDataComponents.FLUID_VARIANT)){
                ImmutableFluidVariant immutableFluidStack = stack.getOrDefault(ModDataComponents.FLUID_VARIANT, new ImmutableFluidVariant(Fluids.EMPTY, 0));
                if (immutableFluidStack.fluid() == Fluids.EMPTY) return;
                FluidStack fluidStack = new FluidStack(immutableFluidStack.fluid(), Math.min(FluidConstants.BUCKET, immutableFluidStack.amount()));
                try(Transaction transaction = Transaction.openOuter()){
                    long fluidFilled = fluidTank.insert(fluidStack.getFluid(), fluidStack.getAmount(), transaction);
                    if (fluidFilled != 0 && (this.getStack(FILL_OUTPUT_SLOT).getItem() == ModBlocks.FLUID_TANK.asItem() || this.getStack(FILL_OUTPUT_SLOT).isEmpty()) && this.getStack(FILL_OUTPUT_SLOT).getCount() < ModBlocks.FLUID_TANK.asItem().getMaxCount()){
                        transaction.commit();

                        FluidStack newFluidStack = fluidStack.copy();
                        if (immutableFluidStack.amount() - fluidFilled <= 0){
                            stack.remove(ModDataComponents.FLUID_VARIANT);
                            this.removeStack(FILL_INPUT_SLOT, 1);
                            ItemStack result = new ItemStack(ModBlocks.FLUID_TANK.asItem());
                            this.inventory.set(FILL_OUTPUT_SLOT, new ItemStack(result.getItem(), this.getStack(FILL_OUTPUT_SLOT).getCount() + result.getCount()));
                        }
                        else {
                            newFluidStack.setAmount(immutableFluidStack.amount() - fluidFilled);
                            stack.set(ModDataComponents.FLUID_VARIANT, new ImmutableFluidVariant(newFluidStack.getFluid().getFluid(), newFluidStack.getAmount()));
                        }
                    }
                    else{
                        transaction.abort();
                    }
                }
            }
        }

        if(this.getStack(DRAIN_INPUT_SLOT).getItem() == Items.BUCKET && fluidTank.amount >= FluidConstants.BUCKET){
            Fluid fluid = fluidTank.getResource().getFluid();
            Item bucketItem = fluid.getBucketItem();

            ItemStack outputStack = this.getStack(DRAIN_OUTPUT_SLOT);

            if (outputStack.isEmpty() || (outputStack.getItem() == bucketItem && outputStack.getCount() < outputStack.getMaxCount())){
                try(Transaction transaction = Transaction.openOuter()){
                    long extract = fluidTank.extract(fluidTank.variant, FluidConstants.BUCKET, transaction);
                    if (extract == FluidConstants.BUCKET){
                        transaction.commit();
                        this.removeStack(DRAIN_INPUT_SLOT, 1);
                        ItemStack result = new ItemStack(bucketItem);
                        this.inventory.set(DRAIN_OUTPUT_SLOT, new ItemStack(result.getItem(), this.getStack(DRAIN_OUTPUT_SLOT).getCount() + result.getCount()));
                    }
                    else{
                        transaction.abort();
                    }
                }
            }
        }
        else if(this.getStack(DRAIN_INPUT_SLOT).getItem() == ModBlocks.FLUID_TANK.asItem() && fluidTank.amount > 0){
            ItemStack stack = this.getStack(DRAIN_INPUT_SLOT);

            if (stack.contains(ModDataComponents.FLUID_VARIANT)){
                ImmutableFluidVariant immutableFluidStack = stack.getOrDefault(ModDataComponents.FLUID_VARIANT, new ImmutableFluidVariant(Fluids.EMPTY, 0));
                FluidStack fluidStack = new FluidStack(immutableFluidStack.fluid(), immutableFluidStack.amount());
                if (fluidTank.variant.getFluid().matchesType(fluidStack.getFluid().getFluid())){
                    if(fluidStack.getAmount() < FluidConstants.BUCKET * 50){
                        try(Transaction transaction = Transaction.openOuter()){
                            long fluidStack2 = fluidTank.extract(fluidTank.variant, Math.min(fluidTank.amount, Math.min(FluidConstants.BUCKET, FluidConstants.BUCKET * 50 - fluidStack.getAmount())), transaction);
                            if (fluidStack2 != 0){
                                transaction.commit();
                                fluidStack.setAmount(fluidStack.getAmount() + fluidStack2);
                                stack.set(ModDataComponents.FLUID_VARIANT, new ImmutableFluidVariant(fluidStack.getFluid().getFluid(), fluidStack.getAmount()));
                            }
                            else{
                                transaction.abort();
                            }
                        }
                    }
                }
            }
            else{
                try(Transaction transaction = Transaction.openOuter()){
                    long fluidStack = fluidTank.extract(fluidTank.variant, Math.min(fluidTank.amount, FluidConstants.BUCKET), transaction);
                    if (fluidStack != 0){
                        transaction.commit();
                        ImmutableFluidVariant immutableFluidStack = new ImmutableFluidVariant(fluidTank.variant.getFluid(), fluidStack);
                        stack.set(ModDataComponents.FLUID_VARIANT, immutableFluidStack);
                    }
                    else{
                        transaction.abort();
                    }
                }
            }
        }

        Optional<RecipeEntry<SolidingRecipe>> recipe = getCurrentRecipe();
        if(hasRecipe() && energyHandler.getAmountStored() >= recipe.get().value().energy()){
            increaseCraftingProgress();
            markDirty(pWorld, pPos, pState);

            if(hasProgressFinished()) {
                energyHandler.removeAmount(recipe.get().value().energy());
                craftItem();
                resetProgress();
                markDirty(pWorld, pPos, pState);
            }
        } else {
            resetProgress();
            markDirty(pWorld, pPos, pState);
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        Optional<RecipeEntry<SolidingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            List<ItemStack> results = recipe.get().value().output();

            try(Transaction transaction = Transaction.openOuter()){
                this.fluidTank.extract(recipe.get().value().fluidStack().getFluid(), recipe.get().value().fluidStack().getAmount(), transaction);
                transaction.commit();
            }

            for (ItemStack result : results) {
                int outputSlot = findSuitableOutputSlot(result);
                if (outputSlot != -1) {
                    this.inventory.set(outputSlot, new ItemStack(result.getItem(), this.getStack(outputSlot).getCount() + result.getCount()));
                } else {
                    System.err.println("No suitable output slot found for item: " + result);
                }
            }
        }
    }

    private int findSuitableOutputSlot(ItemStack result) {
        for (int i : OUTPUT_SLOT) {
            ItemStack stackInSlot = this.getStack(i);
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

        if (this.fluidTank.getAmount() < recipe.get().value().fluidStack().getAmount()) {
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

        for (int j : OUTPUT_SLOT) {
            ItemStack stackInSlot = this.getStack(j);
            if (!stackInSlot.isEmpty()) {
                for (ItemStack result : results) {
                    if (stackInSlot.getItem() == result.getItem()) {
                        if (stackInSlot.getCount() + result.getCount() <= result.getMaxCount()) {
                            emptyCount++;
                        }
                    }
                }
            } else {
                emptyCount++;
            }
        }

        return emptyCount >= count;
    }

    private Optional<RecipeEntry<SolidingRecipe>> getCurrentRecipe(){
        ServerWorld world = (ServerWorld) this.world;
        return world.getRecipeManager().getFirstMatch(ModRecipes.SOLIDING_TYPE, new SingleFluidRecipeInput(new FluidStack(this.fluidTank.variant.getFluid(), this.fluidTank.amount)), world);
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        for (int i : OUTPUT_SLOT) {
            ItemStack stackInSlot = this.getStack(i);
            if (stackInSlot.isEmpty() || (stackInSlot.getItem() == result.getItem() && stackInSlot.getCount() + result.getCount() <= stackInSlot.getMaxCount())) {
                return true;
            }
        }
        return false;
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        for (int i : OUTPUT_SLOT) {
            ItemStack stackInSlot = this.getStack(i);
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        var nbt = super.toInitialChunkDataNbt(registries);
        writeNbt(nbt, registries);
        return nbt;
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        ContainerUtils.dropContents(world, pos, this);
        super.onBlockReplaced(pos, oldState);
    }
}
