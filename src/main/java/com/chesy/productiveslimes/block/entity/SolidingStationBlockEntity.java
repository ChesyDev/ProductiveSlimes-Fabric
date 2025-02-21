package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.item.custom.BucketItem;
import com.chesy.productiveslimes.util.CustomEnergyStorage;
import com.chesy.productiveslimes.recipe.ModRecipes;
import com.chesy.productiveslimes.recipe.SolidingRecipe;
import com.chesy.productiveslimes.screen.custom.SolidingStationMenu;
import com.chesy.productiveslimes.util.IEnergyBlockEntity;
import com.chesy.productiveslimes.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
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

public class SolidingStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IEnergyBlockEntity {
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
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public static final int INPUT_SLOT = 0;
    public static final int[] OUTPUT_SLOT = {1, 2};

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

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
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
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("EnergyInventory", energyHandler.getAmountStored());

        nbt.putInt("soliding_station.progress", progress);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, inventory);
        energyHandler.setAmount(nbt.getInt("EnergyInventory"));

        progress = nbt.getInt("soliding_station.progress");
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == INPUT_SLOT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return Arrays.stream(OUTPUT_SLOT).anyMatch(i -> i == slot);
    }

    public void tick(World pWorld, BlockPos pPos, BlockState pState) {
        Optional<SolidingRecipe> recipe = getCurrentRecipe();
        if(hasRecipe() && energyHandler.getAmountStored() >= recipe.get().getEnergy()){
            increaseCraftingProgress();
            markDirty(pWorld, pPos, pState);

            if(hasProgressFinished()) {
                energyHandler.removeAmount(recipe.get().getEnergy());
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
        Optional<SolidingRecipe> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            List<ItemStack> results = recipe.get().getOutputs();

            this.removeStack(INPUT_SLOT, recipe.get().getInputCount());

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
        Optional<SolidingRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        if (this.getStack(INPUT_SLOT).getCount() < recipe.get().getInputCount()) {
            return false;
        }

        List<ItemStack> results = recipe.get().getOutputs();

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

    private Optional<SolidingRecipe> getCurrentRecipe(){
        ServerWorld world = (ServerWorld) this.world;
        return world.getRecipeManager().getFirstMatch(SolidingRecipe.Type.INSTANCE, new SimpleInventory(this.getStack(INPUT_SLOT)), world);
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

    public FluidVariant getRenderStack() {
        if (inventory.get(INPUT_SLOT).getItem() instanceof BucketItem bucketItem) {
            return bucketItem.getFluidStack();
        }
        return FluidVariant.blank();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
    }
}
