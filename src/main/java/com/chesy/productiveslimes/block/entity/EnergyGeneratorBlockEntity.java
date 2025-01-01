package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.handler.ContainerUtils;
import com.chesy.productiveslimes.handler.CustomEnergyStorage;
import com.chesy.productiveslimes.handler.EnergyAccessingBlock;
import com.chesy.productiveslimes.item.ModItems;
import com.chesy.productiveslimes.screen.custom.EnergyGeneratorMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnergyGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    protected final PropertyDelegate data;

    private final CustomEnergyStorage energyHandler = new CustomEnergyStorage(10000, 0, 100, 0) {
        @Override
        protected void onFinalCommit() {
            markDirty();
        }

        @Override
        public boolean supportsInsertion() {
            return false;
        }

        @Override
        public boolean supportsExtraction() {
            return true;
        }
    };

    private int progress = 0;
    private int maxProgress = 100;

    public CustomEnergyStorage getEnergyHandler() {
        return energyHandler;
    }

    public PropertyDelegate getData() {
        return data;
    }

    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_GENERATOR, pos, state);
        this.data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> EnergyGeneratorBlockEntity.this.energyHandler.getAmountStored();
                    case 1 -> EnergyGeneratorBlockEntity.this.energyHandler.getMaxAmountStored();
                    case 2 -> EnergyGeneratorBlockEntity.this.progress;
                    case 3 -> EnergyGeneratorBlockEntity.this.maxProgress;
                    default -> throw new UnsupportedOperationException("Unexpected value: " + index);
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
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
                if (canBurn(this.inventory.getFirst())) {
                    this.progress = this.maxProgress = getBurnTime(this.inventory.getFirst());
                    this.removeStack(0, 1);
                    isDirty.set(true);
                }
            } else {
                this.progress--;

                int upgrade = 0;
                for (int i = 1; i < this.inventory.size(); i++) {
                    if (this.inventory.get(i).isEmpty()) continue;

                    if (this.inventory.get(i).getItem() == ModItems.ENERGY_MULTIPLIER_UPGRADE) {
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

                EnergyStorage neighborStorage = EnergyStorage.SIDED.find(world, pPos.offset(direction), direction.getOpposite());

                if (neighborStorage != null) {
                    if (neighborStorage.supportsInsertion()) {
                        try (Transaction transaction = Transaction.openOuter()) {
                            long energyToExtract = EnergyStorageUtil.move(
                                    energyHandler,
                                    neighborStorage,
                                    Math.min(energyHandler.amount >= 1000 ? 1000 : energyHandler.amount, neighborStorage.getCapacity() - neighborStorage.getAmount()),
                                    transaction
                            );

                            if (energyToExtract > 0) {
                                transaction.commit();
                                isDirty.set(true);
                            }
                            else {
                                transaction.abort();
                            }
                        }
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

        Inventories.writeNbt(nbt, inventory, registries);
        nbt.put("Energy", energyHandler.serializeNBT(registries));
        nbt.putInt("Progress", progress);
        nbt.putInt("MaxProgress", maxProgress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        Inventories.readNbt(nbt, inventory, registries);
        this.energyHandler.deserializeNBT(registries, nbt.getCompound("Energy"));
        this.progress = nbt.getInt("Progress");
        this.maxProgress = nbt.getInt("MaxProgress");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void drops() {
        SimpleInventory inventory = new SimpleInventory(this.inventory.size());
        for (int i = 0; i < this.inventory.size(); i++) {
            inventory.setStack(i, this.inventory.get(i));
        }
        assert world != null;
        ContainerUtils.dropContents(world, this.pos, inventory);
    }

    private void sendUpdate() {
        markDirty();

        if (this.world != null)
            this.world.updateListeners(this.pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public int getBurnTime(ItemStack stack) {
        if (stack.getItem() == ModBlocks.ENERGY_SLIME_BLOCK.asItem()) {
            return 1000;
        } else if (stack.getItem() == ProductiveSlimes.ENERGY_SLIME_BALL) {
            return 100;
        }

        return 0;
    }

    public boolean canBurn(ItemStack stack) {
        return getBurnTime(stack) > 0;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.pos;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
}
