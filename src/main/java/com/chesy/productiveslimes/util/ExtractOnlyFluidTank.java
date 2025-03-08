package com.chesy.productiveslimes.util;

import com.chesy.productiveslimes.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public class ExtractOnlyFluidTank extends SingleVariantStorage<FluidVariant> {
    private long capacity;

    public ExtractOnlyFluidTank(long capacity) {
        this.capacity = capacity;
    }

    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant fluidVariant) {
        return capacity;
    }

    public FluidStack getFluid() {
        return new FluidStack(variant.getFluid(), amount);
    }

    public long getFluidAmount(){
        return getFluid().getAmount();
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        SingleVariantStorage.readNbt(this, FluidVariant.CODEC, FluidVariant::blank, nbt, wrapperLookup);
    }

    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        SingleVariantStorage.writeNbt(this, FluidVariant.CODEC, nbt, wrapperLookup);
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        return super.extract(extractedVariant, maxAmount, transaction);
    }

    public long internalFill(FluidStack resource, TransactionContext action){
        StoragePreconditions.notBlankNotNegative(resource.getFluid(), resource.getAmount());
        if ((resource.getFluid().equals(this.variant) || this.variant.isBlank()) && this.canInsert(resource.getFluid())) {
            long insertedAmount = Math.min(resource.getAmount(), this.getCapacity(resource.getFluid()) - this.amount);
            if (insertedAmount > 0L) {
                this.updateSnapshots(action);
                if (this.variant.isBlank()) {
                    this.variant = resource.getFluid();
                    this.amount = insertedAmount;
                } else {
                    this.amount += insertedAmount;
                }

                return insertedAmount;
            }
        }

        return 0L;
    }
}
