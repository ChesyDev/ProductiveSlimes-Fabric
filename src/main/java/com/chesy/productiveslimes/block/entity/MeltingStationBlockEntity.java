package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.datacomponent.ModDataComponents;
import com.chesy.productiveslimes.datacomponent.custom.ImmutableFluidVariant;
import com.chesy.productiveslimes.util.*;
import com.chesy.productiveslimes.recipe.MeltingRecipe;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.screen.custom.MeltingStationMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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

import java.util.Optional;

public class MeltingStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory, IEnergyBlockEntity, IFluidBlockEntity {
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
    private final ExtractOnlyFluidTank outputHandler = new ExtractOnlyFluidTank(FluidConstants.BUCKET * 16) {
        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            markDirty();
            if (world != null) {
                world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            }
        }
    };

    protected final PropertyDelegate data;
    private int progress = 0;
    private int maxProgress = 78;

    private final int DRAIN_INPUT_SLOT = 0;
    private final int DRAIN_OUTPUT_SLOT = 1;
    private final int INPUT_SLOT = 2;


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

    public ExtractOnlyFluidTank getFluidHandler() {
        return outputHandler;
    }

    public FluidStack getFluidStack() {
        return new FluidStack(outputHandler.variant.getFluid(), outputHandler.getAmount());
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
        outputHandler.writeNbt(nbt, registries);
        nbt.putInt("melting_station.progress", progress);

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        Inventories.readNbt(nbt, inventory, registries);
        energyHandler.setAmount(nbt.getInt("EnergyInventory"));
        outputHandler.readNbt(nbt, registries);
        progress = nbt.getInt("melting_station.progress");
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == INPUT_SLOT;
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
        return slot == DRAIN_OUTPUT_SLOT;
    }

    public void tick(World pWorld, BlockPos pPos, BlockState pState) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = this.getPos().offset(direction);
            Storage<FluidVariant> neighborStorage = FluidStorage.SIDED.find(world, neighborPos, direction.getOpposite());

            if (neighborStorage != null) {
                try(Transaction transaction = Transaction.openOuter()){
                    FluidStack availableFluid = outputHandler.getFluid();

                    if (availableFluid.isEmpty()) {
                        continue;
                    }

                    long neighborFluid = neighborStorage.insert(availableFluid.copy().getFluid(), Math.min(FluidConstants.BUCKET, availableFluid.getAmount()), transaction);
                    if (!availableFluid.isEmpty() && neighborFluid > 0) {
                        long drained = outputHandler.extract(availableFluid.getFluid(), neighborFluid, transaction);
                        if (drained > 0) {
                            transaction.commit();
                        }
                        else{
                            transaction.abort();
                        }
                    }
                }
            }
        }


        if(this.getStack(DRAIN_INPUT_SLOT).getItem() == Items.BUCKET && outputHandler.amount >= FluidConstants.BUCKET){
            Fluid fluid = outputHandler.getResource().getFluid();
            Item bucketItem = fluid.getBucketItem();

            ItemStack outputStack = this.getStack(DRAIN_OUTPUT_SLOT);

            if (outputStack.isEmpty() || (outputStack.getItem() == bucketItem && outputStack.getCount() < outputStack.getMaxCount())){
                try(Transaction transaction = Transaction.openOuter()){
                    long extract = outputHandler.extract(outputHandler.variant, FluidConstants.BUCKET, transaction);
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
        else if(this.getStack(DRAIN_INPUT_SLOT).getItem() == ModBlocks.FLUID_TANK.asItem() && outputHandler.amount > 0){
            ItemStack stack = this.getStack(DRAIN_INPUT_SLOT);

            if (stack.contains(ModDataComponents.FLUID_VARIANT)){
                ImmutableFluidVariant immutableFluidStack = stack.getOrDefault(ModDataComponents.FLUID_VARIANT, new ImmutableFluidVariant(Fluids.EMPTY, 0));
                FluidStack fluidStack = new FluidStack(immutableFluidStack.fluid(), immutableFluidStack.amount());
                if (outputHandler.variant.getFluid().matchesType(fluidStack.getFluid().getFluid())){
                    if(fluidStack.getAmount() < FluidConstants.BUCKET * 50){
                        try(Transaction transaction = Transaction.openOuter()){
                            long fluidStack2 = outputHandler.extract(outputHandler.variant, Math.min(outputHandler.amount, Math.min(FluidConstants.BUCKET, FluidConstants.BUCKET * 50 - fluidStack.getAmount())), transaction);
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
                    long fluidStack = outputHandler.extract(outputHandler.variant, Math.min(outputHandler.amount, FluidConstants.BUCKET), transaction);
                    if (fluidStack != 0){
                        transaction.commit();
                        ImmutableFluidVariant immutableFluidStack = new ImmutableFluidVariant(outputHandler.variant.getFluid(), fluidStack);
                        stack.set(ModDataComponents.FLUID_VARIANT, immutableFluidStack);
                    }
                    else{
                        transaction.abort();
                    }
                }
            }
        }

        Optional<RecipeEntry<MeltingRecipe>> recipe = getCurrentRecipe();
        if(hasRecipe() && energyHandler.getAmountStored() >= recipe.get().value().energy()){
            increaseCraftingProgress();
            markDirty(pWorld, pPos, pState);

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
        Optional<RecipeEntry<MeltingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            FluidStack results = recipe.get().value().output();

            this.removeStack(INPUT_SLOT, recipe.get().value().inputItems().count());

            try(Transaction transaction = Transaction.openOuter()){
                this.outputHandler.internalFill(results, transaction);
                transaction.commit();
            }
        }
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<MeltingRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        if (this.getStack(INPUT_SLOT).getCount() < recipe.get().value().inputItems().count()) {
            return false;
        }

        FluidStack result = recipe.get().value().output();

        if (!canInsertAmountIntoOutputSlot(result) || !canInsertItemIntoOutputSlot(result)) {
            return false;
        }

        return checkSlot(result);
    }

    private boolean checkSlot(FluidStack results){
        return outputHandler.getResource().isBlank() || outputHandler.getResource().getFluid().matchesType(results.getFluid().getFluid()) && outputHandler.getAmount() + results.getAmount() <= outputHandler.getCapacity();
    }

    private Optional<RecipeEntry<MeltingRecipe>> getCurrentRecipe(){
        ServerWorld world = (ServerWorld) this.world;
        return world.getRecipeManager().getFirstMatch(ModRecipes.MELTING_TYPE, new SingleStackRecipeInput(this.getStack(INPUT_SLOT)), world);
    }

    private boolean canInsertAmountIntoOutputSlot(FluidStack result) {
        return outputHandler.getAmount() + result.getAmount() <= outputHandler.getCapacity();
    }

    private boolean canInsertItemIntoOutputSlot(FluidStack item) {
        return outputHandler.getResource().isBlank() || outputHandler.getResource().getFluid().matchesType(item.getFluid().getFluid());
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
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        ContainerUtils.dropContents(world, pos, this);
        super.onBlockReplaced(pos, oldState);
    }
}
