package com.chesy.productiveslimes.mixin;

import com.chesy.productiveslimes.util.MixinAdditionMethod;
import com.chesy.productiveslimes.util.PackRepositoryAccess;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin implements MixinAdditionMethod, PackRepositoryAccess {
    @Shadow
    public final Set<ResourcePackProvider> providers;
    @Shadow public void scanPacks() {}

    public ResourcePackManagerMixin(Set<ResourcePackProvider> providers) {
        this.providers = providers;
    }

    @Override
    public synchronized void addPackFinder(ResourcePackProvider packFinder) {
        providers.add(packFinder);
    }

    @Override
    public void addRepositorySource(ResourcePackProvider source) {
        this.providers.add(source);
        this.scanPacks();
    }
}
