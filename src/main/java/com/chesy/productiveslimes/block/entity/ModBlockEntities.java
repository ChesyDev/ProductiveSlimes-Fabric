package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.block.custom.SolidingStationBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;

public class ModBlockEntities {
    public static final BlockEntityType<MeltingStationBlockEntity> MELTING_STATION = register(
            "melting_station",
            FabricBlockEntityTypeBuilder.create(MeltingStationBlockEntity::new, ModBlocks.MELTING_STATION).build()
    );

    public static final BlockEntityType<SolidingStationBlockEntity> SOLIDING_STATION = register(
            "soliding_station",
            FabricBlockEntityTypeBuilder.create(SolidingStationBlockEntity::new, ModBlocks.SOLIDING_STATION).build()
    );

    public static final BlockEntityType<DnaExtractorBlockEntity> DNA_EXTRACTOR = register(
            "dna_extractor",
            FabricBlockEntityTypeBuilder.create(DnaExtractorBlockEntity::new, ModBlocks.DNA_EXTRACTOR).build()
    );

    public static final BlockEntityType<DnaSynthesizerBlockEntity> DNA_SYNTHESIZER = register(
            "dna_synthesizer",
            FabricBlockEntityTypeBuilder.create(DnaSynthesizerBlockEntity::new, ModBlocks.DNA_SYNTHESIZER).build()
    );

    public static final BlockEntityType<EnergyGeneratorBlockEntity> ENERGY_GENERATOR = register(
            "energy_generator",
            FabricBlockEntityTypeBuilder.create(EnergyGeneratorBlockEntity::new, ModBlocks.ENERGY_GENERATOR).build()
    );

    public static final BlockEntityType<SlimeSqueezerBlockEntity> SLIME_SQUEEZER = register(
            "slime_squeezer",
            FabricBlockEntityTypeBuilder.create(SlimeSqueezerBlockEntity::new, ModBlocks.SLIME_SQUEEZER).build()
    );

    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK = register(
            "fluid_tank",
            FabricBlockEntityTypeBuilder.create(FluidTankBlockEntity::new, ModBlocks.FLUID_TANK).build()
    );

    public static final BlockEntityType<CableBlockEntity> CABLE = register(
            "cable",
            FabricBlockEntityTypeBuilder.create(CableBlockEntity::new, ModBlocks.CABLE).build()
    );

    public static <T extends BlockEntityType<?>> T register(String name, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ProductiveSlimes.MOD_ID, name), blockEntityType);
    }

    public static void initialize() {
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            if (blockEntity instanceof EnergyGeneratorBlockEntity energyGeneratorBlockEntity) {
                return energyGeneratorBlockEntity.getEnergyHandler();
            }

            if (blockEntity instanceof MeltingStationBlockEntity meltingStationBlockEntity) {
                return meltingStationBlockEntity.getEnergyHandler();
            }
            return null;
        }, ModBlockEntities.ENERGY_GENERATOR, ModBlockEntities.MELTING_STATION);
    }
}
