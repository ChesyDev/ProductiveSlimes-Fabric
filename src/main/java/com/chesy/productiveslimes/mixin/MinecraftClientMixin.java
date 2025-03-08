package com.chesy.productiveslimes.mixin;

import com.chesy.productiveslimes.config.CustomVariantRegistry;
import com.chesy.productiveslimes.config.asset.CustomVariantResourcePack;
import com.chesy.productiveslimes.util.PackRepositoryAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Final private ResourcePackManager resourcePackManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // Create the custom resource pack
        CustomVariantResourcePack resourcePack = new CustomVariantResourcePack(CustomVariantRegistry.resourceData);
        ResourcePackProfile pack = ResourcePackProfile.create(
                new ResourcePackInfo(
                        "productiveslimes_resourcepack",
                        Text.literal("In Memory Resource Pack"),
                        ResourcePackSource.BUILTIN,
                        Optional.empty()
                ),
                new ResourcePackProfile.PackFactory() {
                    @Override
                    public ResourcePack open(ResourcePackInfo info) {
                        return resourcePack;
                    }

                    @Override
                    public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                        return resourcePack;
                    }
                },
                ResourceType.CLIENT_RESOURCES,
                new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true)
        );

        // Add it to the repository
        ResourcePackProvider customSource = (packSelectionConfig) -> packSelectionConfig.accept(pack);
        ((PackRepositoryAccess) this.resourcePackManager).addRepositorySource(customSource);

        // Ensure it’s selected
        List<String> selectedIds = new ArrayList<>(this.resourcePackManager.getEnabledIds());
        String packId = "productiveslimes_resourcepack";
        if (!selectedIds.contains(packId)) {
            selectedIds.add(packId);
            this.resourcePackManager.setEnabledProfiles(selectedIds);
        }
    }
}
