package com.chesy.productiveslimes.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;

public interface IFluidBlockEntity {
    SingleVariantStorage<FluidVariant> getFluidHandler();
}
