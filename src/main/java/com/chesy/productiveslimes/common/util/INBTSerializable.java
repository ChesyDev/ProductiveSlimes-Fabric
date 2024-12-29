package com.chesy.productiveslimes.common.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.UnknownNullability;

public interface INBTSerializable<T extends NbtCompound>{
    @UnknownNullability
    T serializeNBT(RegistryWrapper.WrapperLookup registryWrapper);

    void deserializeNBT(RegistryWrapper.WrapperLookup registryWrapper, T nbt);
}
