package com.chesy.productiveslimes.handler;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@
Amount = Energy
Insert = Receive
@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 */

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
}
