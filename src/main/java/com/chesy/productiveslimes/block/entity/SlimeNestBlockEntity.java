package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.item.custom.NestUpgradeItem;
import com.chesy.productiveslimes.screen.custom.SlimeNestMenu;
import com.chesy.productiveslimes.util.ImplementedInventory;
import com.chesy.productiveslimes.util.SlimeData;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SlimeNestBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private SlimeData slimeData;
    private int cooldown = 0;
    private int counter = 0;
    private ItemStack dropItem = ItemStack.EMPTY;
    private final PropertyDelegate data;
    private int hasSlot = 1;
    private int tick = 0;
    private float multiplier = 1;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(14, ItemStack.EMPTY);

    public final int[] upgradeSlot = new int[]{0, 1, 2, 3};
    public final int[] slimeSlot = new int[]{4};
    public final int[] outputSlot = new int[]{5, 6, 7, 8, 9, 10, 11, 12, 13};

    public SlimeNestBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SLIME_NEST, pos, blockState);
        this.data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0:
                        return SlimeNestBlockEntity.this.counter;
                    case 1:
                        return SlimeNestBlockEntity.this.cooldown;
                    case 2:
                        return SlimeNestBlockEntity.this.hasSlot;
                    case 3:
                        return SlimeNestBlockEntity.this.slimeData != null ? SlimeNestBlockEntity.this.slimeData.size() : 0;
                    case 4:
                        return SlimeNestBlockEntity.this.tick;
                    case 5:
                        return (int) (SlimeNestBlockEntity.this.multiplier * 1000);
                    default:
                        return 0;
                }
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        SlimeNestBlockEntity.this.counter = value;
                        break;
                    case 1:
                        SlimeNestBlockEntity.this.cooldown = value;
                        break;
                    case 2:
                        SlimeNestBlockEntity.this.hasSlot = value;
                        break;
                }
            }

            @Override
            public int size() {
                return 6;
            }
        };
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ImplementedInventory.super.setStack(slot, stack);
        if (slot == slimeSlot[0]) {
            if (stack.isEmpty()){
                slimeData = null;
                dropItem = ItemStack.EMPTY;
            }
            else{
                if (stack.getNbt() != null &&  stack.getNbt().contains("slime_data")){
                    slimeData = SlimeData.fromTag(stack.getNbt().getCompound("slime_data"));
                    assert slimeData != null;
                    cooldown = slimeData.cooldown();
                    dropItem = slimeData.dropItem();
                }
            }
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.productiveslimes.slime_nest");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("counter", counter);
        nbt.putInt("cooldown", cooldown);
        if (slimeData != null && !dropItem.isEmpty()) {
            nbt.put("dropItem", dropItem.writeNbt(new NbtCompound()));
            nbt.put("slimeData", slimeData.toTag(new NbtCompound()));
        }
        nbt.putInt("tick", tick);
        nbt.putFloat("multiplier", multiplier);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        counter = nbt.getInt("counter");
        cooldown = nbt.getInt("cooldown");
        dropItem = ItemStack.fromNbt(nbt.getCompound("dropItem"));
        slimeData = SlimeData.fromTag(nbt.getCompound("slimeData"));
        tick = nbt.getInt("tick");
        multiplier = nbt.getFloat("multiplier");
    }

    public void tick(World level, BlockPos pos, BlockState state) {
        data.set(2, 1);
        if (inventory.get(slimeSlot[0]).isEmpty()) {
            counter = 0;
            cooldown = 0;
            dropItem = ItemStack.EMPTY;
            return;
        }
        float speed = 1;
        cooldown = slimeData.cooldown();
        for (int i : upgradeSlot) {
            if (inventory.get(i).getItem() instanceof NestUpgradeItem nestUpgradeItem) {
                speed *= nestUpgradeItem.getMultiplier();
            }
        }
        multiplier = speed;
        tick += 3;
        cooldown = (int) Math.ceil(cooldown / speed);
        markDirty();
        if (level != null && !level.isClient) {
            level.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
        if (!hasAvailableSlot(dropItem)) {
            data.set(2, 0);
            return;
        }
        if (slimeData != null) {
            counter++;
            if (counter >= cooldown) {
                counter = 0;
                int outputSlot = findSuitableSlot(dropItem);
                if (outputSlot != -1) {
                    int stackSize = inventory.get(outputSlot).getCount() + slimeData.size();
                    if (stackSize > dropItem.getMaxCount()) {
                        stackSize = dropItem.getMaxCount();
                    }
                    inventory.set(outputSlot, new ItemStack(dropItem.copy().getItem(), stackSize));
                }
            }
        }
        if (tick % 20 == 0) {
            level.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.BLOCKS, 0.5F, 1.0F);
        }
    }

    private boolean hasAvailableSlot(ItemStack stack) {
        for (int i : outputSlot) {
            if (inventory.get(i).isEmpty() || (inventory.get(i).getItem() == stack.getItem() && inventory.get(i).getCount() < inventory.get(i).getMaxCount())) {
                return true;
            }
        }
        return false;
    }

    private int findSuitableSlot(ItemStack stack) {
        for (int i : outputSlot) {
            if (inventory.get(i).isEmpty() || (inventory.get(i).getItem() == stack.getItem() && inventory.get(i).getCount() < inventory.get(i).getMaxCount())) {
                return i;
            }
        }
        return -1;
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

    public ItemStack getSlime() {
        return inventory.get(slimeSlot[0]);
    }

    public List<ItemStack> getOutput() {
        List<ItemStack> output = new ArrayList<>();
        for (int i : outputSlot) {
            output.add(inventory.get(i));
        }
        return output;
    }

    public PropertyDelegate getData() {
        return data;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SlimeNestMenu(syncId, playerInventory, this, data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
    }
}