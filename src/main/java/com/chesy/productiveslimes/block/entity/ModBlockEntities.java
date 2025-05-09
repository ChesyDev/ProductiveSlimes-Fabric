package com.chesy.productiveslimes.block.entity;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.util.IEnergyBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;

public class ModBlockEntities {
    public static final BlockEntityType<MeltingStationBlockEntity> MELTING_STATION = register("melting_station", FabricBlockEntityTypeBuilder.create(MeltingStationBlockEntity::new, ModBlocks.MELTING_STATION).build());
    public static final BlockEntityType<SolidingStationBlockEntity> SOLIDING_STATION = register("soliding_station", FabricBlockEntityTypeBuilder.create(SolidingStationBlockEntity::new, ModBlocks.SOLIDING_STATION).build());
    public static final BlockEntityType<DnaExtractorBlockEntity> DNA_EXTRACTOR = register("dna_extractor", FabricBlockEntityTypeBuilder.create(DnaExtractorBlockEntity::new, ModBlocks.DNA_EXTRACTOR).build());
    public static final BlockEntityType<DnaSynthesizerBlockEntity> DNA_SYNTHESIZER = register("dna_synthesizer", FabricBlockEntityTypeBuilder.create(DnaSynthesizerBlockEntity::new, ModBlocks.DNA_SYNTHESIZER).build());
    public static final BlockEntityType<EnergyGeneratorBlockEntity> ENERGY_GENERATOR = register("energy_generator", FabricBlockEntityTypeBuilder.create(EnergyGeneratorBlockEntity::new, ModBlocks.ENERGY_GENERATOR).build());
    public static final BlockEntityType<SlimeSqueezerBlockEntity> SLIME_SQUEEZER = register("slime_squeezer", FabricBlockEntityTypeBuilder.create(SlimeSqueezerBlockEntity::new, ModBlocks.SLIME_SQUEEZER).build());
    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK = register("fluid_tank", FabricBlockEntityTypeBuilder.create(FluidTankBlockEntity::new, ModBlocks.FLUID_TANK).build());
    public static final BlockEntityType<CableBlockEntity> CABLE = register("cable", FabricBlockEntityTypeBuilder.create(CableBlockEntity::new, ModBlocks.CABLE).build());
    public static final BlockEntityType<PipeBlockEntity> PIPE = register("pipe", FabricBlockEntityTypeBuilder.create(PipeBlockEntity::new, ModBlocks.PIPE).build());
    public static final BlockEntityType<SlimeballCollectorBlockEntity> SLIMEBALL_COLLECTOR = register("slimeball_collector", FabricBlockEntityTypeBuilder.create(SlimeballCollectorBlockEntity::new, ModBlocks.SLIMEBALL_COLLECTOR).build());
    public static final BlockEntityType<SlimeNestBlockEntity> SLIME_NEST = register("slime_nest", FabricBlockEntityTypeBuilder.create(SlimeNestBlockEntity::new, ModBlocks.SLIME_NEST).build());

    public static <T extends BlockEntityType<?>> T register(String name, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ProductiveSlimes.MODID, name), blockEntityType);
    }

    public static void initialize() {
        EnergyStorage.SIDED.registerForBlockEntities(
                (blockEntity, direction) -> blockEntity instanceof IEnergyBlockEntity energyBlockEntity? energyBlockEntity.getEnergyHandler() : null,
                ModBlockEntities.ENERGY_GENERATOR,
                ModBlockEntities.MELTING_STATION,
                ModBlockEntities.SOLIDING_STATION,
                ModBlockEntities.CABLE,
                ModBlockEntities.DNA_EXTRACTOR,
                ModBlockEntities.DNA_SYNTHESIZER,
                ModBlockEntities.SLIME_SQUEEZER
        );

        FluidStorage.SIDED.registerForBlockEntities(
                (blockEntity, direction) -> {
                    if (blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity) {
                        return fluidTankBlockEntity.getFluidStorage();
                    }

                    if (blockEntity instanceof SolidingStationBlockEntity solidingStationBlockEntity){
                        return solidingStationBlockEntity.getFluidTank();
                    }

                    if (blockEntity instanceof MeltingStationBlockEntity meltingStationBlockEntity){
                        return meltingStationBlockEntity.getFluidHandler();
                    }

                    if (blockEntity instanceof PipeBlockEntity pipeBlockEntity){
                        return pipeBlockEntity.fluidStorage;
                    }

                    return null;
                },
                ModBlockEntities.FLUID_TANK,
                ModBlockEntities.SOLIDING_STATION,
                ModBlockEntities.MELTING_STATION,
                ModBlockEntities.PIPE
        );
    }
}
