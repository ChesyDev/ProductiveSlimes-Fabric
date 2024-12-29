package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.handler.ContainerUtils;
import com.chesy.productiveslimes.handler.CustomEnergyStorage;
import com.chesy.productiveslimes.handler.EnergyAccessingBlock;
import com.chesy.productiveslimes.handler.ItemStackHandler;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.screen.custom.EnergyGeneratorMenu;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

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
        return Text.translatable("block.productiveslimes.energy_generator");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new EnergyGeneratorMenu(syncId, playerInventory, this, this.data);
    }

    public void tick(World pWorld, BlockPos pPos, BlockState pState) {
        if (this.world == null || this.world.isClient())
            return;

        AtomicBoolean isDirty = new AtomicBoolean(false);

        if (this.energyHandler.getAmountStored() < this.energyHandler.getMaxAmountStored()) {
            if (this.progress <= 0) {
                if (canBurn(this.itemHandler.getStackInSlot(0))) {
                    this.progress = this.maxProgress = getBurnTime(this.itemHandler.getStackInSlot(0));
                    this.itemHandler.getStackInSlot(0).decrement(1);
                    isDirty.set(true);
                }
            } else {
                this.progress--;

                int upgrade = 0;
                for (int i = 0; i < this.upgradeHandler.getSlots(); i++) {
                    if (this.upgradeHandler.getStackInSlot(i).isEmpty()) continue;

                    if (this.upgradeHandler.getStackInSlot(i).getItem() == ModItems.ENERGY_MULTIPLIER_UPGRADE) {
                        upgrade++;
                    }
                }

                int energy = switch (upgrade) {
                    case 1 -> 10;
                    case 2 -> 15;
                    case 3 -> 25;
                    case 4 -> 40;
                    default -> 5;
                };

                this.energyHandler.addAmount(energy);
                isDirty.set(true);
            }
        }

        if (!this.world.isClient) {
            for (Direction direction : Direction.values()) {
                World world = this.world;
                Optional<EnergyStorage> neighborEnergy = new EnergyAccessingBlock(world).getNeighborEnergyStorage(pos, direction);

                if (neighborEnergy.isPresent()) {
                    EnergyStorage neighborStorage = neighborEnergy.get();
                    if (neighborStorage.supportsInsertion()) {
                        int energyToExtract = (int) Math.min(this.energyHandler.extract(1000, Transaction.openOuter()), neighborStorage.insert(1000, Transaction.openOuter()));

                        this.energyHandler.extract(energyToExtract, Transaction.openOuter());
                        neighborStorage.insert(energyToExtract, Transaction.openOuter());
                    }
                }
            }
        }

        if (isDirty.get()) {
            sendUpdate();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        nbt.put("Inventory", itemHandler.serializeNBT(registries));
        nbt.put("Energy", energyHandler.serializeNBT(registries));
        nbt.putInt("Progress", progress);
        nbt.putInt("MaxProgress", maxProgress);
        nbt.put("Upgrades", upgradeHandler.serializeNBT(registries));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        this.itemHandler.deserializeNBT(registries, nbt.getCompound("Inventory"));
        this.energyHandler.deserializeNBT(registries, nbt.getCompound("Energy"));
        this.progress = nbt.getInt("Progress");
        this.maxProgress = nbt.getInt("MaxProgress");
        this.upgradeHandler.deserializeNBT(registries, nbt.getCompound("Upgrades"));
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void drops() {
        SimpleInventory inventory = new SimpleInventory(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setStack(i, itemHandler.getStackInSlot(i));
        }
        ContainerUtils.dropContents(this.world, this.pos, inventory);
    }

    private void sendUpdate() {
        markDirty();

        if(this.world != null)
            this.world.updateListeners(this.pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

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
