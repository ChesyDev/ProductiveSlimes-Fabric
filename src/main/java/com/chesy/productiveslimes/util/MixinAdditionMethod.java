package com.chesy.productiveslimes.util;

import net.minecraft.resource.ResourcePackProvider;

public interface MixinAdditionMethod {
    void addPackFinder(ResourcePackProvider packFinder);
}
