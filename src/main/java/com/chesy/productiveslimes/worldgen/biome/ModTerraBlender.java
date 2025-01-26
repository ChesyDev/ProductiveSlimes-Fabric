package com.chesy.productiveslimes.worldgen.biome;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.util.Identifier;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.TerraBlenderApi;

public class ModTerraBlender implements TerraBlenderApi {
    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new ModOverworldRegion(Identifier.of(ProductiveSlimes.MODID, "slimy_land"), RegionType.OVERWORLD, 5));
    }
}
