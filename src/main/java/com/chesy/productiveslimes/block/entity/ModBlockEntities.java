package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<MeltingStationBlockEntity> MELTING_STATION = register(
            "melting_station",
            FabricBlockEntityTypeBuilder.create(MeltingStationBlockEntity::new, ModBlocks.MELTING_STATION).build()
    );

    public static <T extends BlockEntityType<?>> T register(String name, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ProductiveSlimes.MOD_ID, name), blockEntityType);
    }

    public static void initialize() {
    }
}
