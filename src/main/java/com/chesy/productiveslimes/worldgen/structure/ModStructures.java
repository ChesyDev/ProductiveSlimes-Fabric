package com.chesy.productiveslimes.worldgen.structure;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.worldgen.biome.ModBiomes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ModStructures {
    public static final RegistryKey<Structure> SLIMY_VILLAGE = RegistryKey.of(RegistryKeys.STRUCTURE, Identifier.of(ProductiveSlimes.MODID, "slimy_village"));

    public static void bootstrap(Registerable<Structure> context){
        context.register(SLIMY_VILLAGE, slimyVillage(context));
    }

    public static Structure slimyVillage(Registerable<Structure> context){
        RegistryEntryLookup<Biome> biomeSettings = context.getRegistryLookup(RegistryKeys.BIOME);
        RegistryEntryLookup<StructurePool> templatePoolSettings = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);

        return new JigsawStructure(
                new Structure.Config(
                        RegistryEntryList.of(biomeSettings.getOrThrow(ModBiomes.SLIMY_LAND)),
                        new HashMap<>(),
                        GenerationStep.Feature.SURFACE_STRUCTURES,
                        StructureTerrainAdaptation.BEARD_THIN
                ),
                templatePoolSettings.getOrThrow(ModStructureTemplatePools.VILLAGE_CENTER),
                Optional.empty(),
                7,
                ConstantHeightProvider.create(YOffset.fixed(0)),
                false,
                Optional.of(Heightmap.Type.WORLD_SURFACE_WG),
                116,
                List.of(),
                DimensionPadding.NONE,
                StructureLiquidSettings.APPLY_WATERLOGGING
        );
    }
}