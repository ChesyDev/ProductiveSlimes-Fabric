package com.chesy.productiveslimes.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class CustomEnergyStorage extends SimpleEnergyStorage {

    public CustomEnergyStorage(long capacity, long maxInsert, long maxExtract, long amount) {
        super(capacity, maxInsert, maxExtract);
        this.amount = amount;
    }

    public void setAmount(long amount) {
        if(amount < 0)
            amount = 0;

        if(amount > this.capacity)
            amount = this.capacity;

        this.amount = amount;
    }

    public int getAmountStored() {
        return (int)this.amount;
    }

    public int getMaxAmountStored() {
        return (int)this.capacity;
    }

    public void addAmount(int amount) {
        setAmount(this.amount + (long)amount);
    }

    public void removeAmount(int amount) {
        setAmount(this.amount - (long)amount);
    }

    public NbtCompound serializeNBT(RegistryWrapper.WrapperLookup registryWrapper) {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("amount", this.getAmountStored());
        return nbt;
    }

    public void deserializeNBT(RegistryWrapper.WrapperLookup registryWrapper, NbtCompound nbt) {
        setAmount(nbt.getInt("amount", 0));
    }
}
