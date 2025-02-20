package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.screen.custom.SlimeballCollectorMenu;
import com.chesy.productiveslimes.util.ImplementedInventory;
import com.chesy.productiveslimes.util.ModTags;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlimeballCollectorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private static final int RANGE_XZ = 8;
    private static final int RANGE_Y = 256;
    private int enableOutline = 0;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private final PropertyDelegate data;

    public SlimeballCollectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SLIMEBALL_COLLECTOR, pos, blockState);
        this.data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> enableOutline;
                    case 1 -> 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    public PropertyDelegate getData() {
        return data;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.productiveslimes.slimeball_collector");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("enableOutline", enableOutline);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        enableOutline = nbt.getInt("enableOutline");
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return false;
    }

    public void tick(World level, BlockPos pos, BlockState state) {
        if (this.world == null || this.world.isClient) return;

        // Define the collection area: 16x16 in X and Z, full height in Y.
        Box collectionArea = new Box(
                pos.getX() - RANGE_XZ, -64, pos.getZ() - RANGE_XZ,
                pos.getX() + RANGE_XZ + 1, RANGE_Y, pos.getZ() + RANGE_XZ + 1
        );
        // Find all dropped items in the collection area.
        List<ItemEntity> items = this.world.getNonSpectatingEntities(ItemEntity.class, collectionArea);
        for (ItemEntity item : items) {
            if (!item.isRemoved() && item.getStack().isIn(ModTags.Items.SLIME_BALLS)) {
                collectItem(item);
            }
        }
    }

    private void collectItem(ItemEntity item) {
        if (!hasSpaceForItem(item.getStack())) {
            return;
        }
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isEmpty()) {
                inventory.set(i, item.getStack());
                item.remove(Entity.RemovalReason.KILLED);
                return;
            } else if (inventory.get(i).isOf(item.getStack().getItem()) && inventory.get(i).getCount() + item.getStack().getCount() <= inventory.get(i).getMaxCount()) {
                inventory.get(i).increment(item.getStack().getCount());
                item.remove(Entity.RemovalReason.KILLED);
                return;
            }
        }
    }

    private boolean hasSpaceForItem(ItemStack stack) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isEmpty() || inventory.get(i).isOf(stack.getItem()) && inventory.get(i).getCount() + stack.getCount() <= inventory.get(i).getMaxCount()) {
                return true;
            }
        }
        return false;
    }

    public void setEnableOutline(int enableOutline) {
        this.enableOutline = enableOutline;
        markDirty();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SlimeballCollectorMenu(syncId, playerInventory, this, data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
    }
}