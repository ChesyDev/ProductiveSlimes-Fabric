package com.chesy.productiveslimes.mixin;

import com.chesy.productiveslimes.util.MixinAdditionMethod;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin implements MixinAdditionMethod {
    @Shadow
    public final Set<ResourcePackProvider> providers;

    public ResourcePackManagerMixin(Set<ResourcePackProvider> providers) {
        this.providers = providers;
    }

    @Override
    public synchronized void addPackFinder(ResourcePackProvider packFinder) {
        providers.add(packFinder);
    }
}
