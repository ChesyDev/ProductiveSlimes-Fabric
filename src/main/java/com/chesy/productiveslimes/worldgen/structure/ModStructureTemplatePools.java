package com.chesy.productiveslimes.worldgen.structure;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.pool.*;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ModStructureTemplatePools {
    public static final RegistryKey<StructurePool> BUILDINGS = RegistryKey.of(RegistryKeys.TEMPLATE_POOL, Identifier.of(ProductiveSlimes.MODID, "slimy_village/buildings"));
    public static final RegistryKey<StructurePool> STREETS = RegistryKey.of(RegistryKeys.TEMPLATE_POOL, Identifier.of(ProductiveSlimes.MODID, "slimy_village/streets"));
    public static final RegistryKey<StructurePool> VILLAGE_CENTER = RegistryKey.of(RegistryKeys.TEMPLATE_POOL, Identifier.of(ProductiveSlimes.MODID, "slimy_village/village_center"));

    public static void bootstrap(Registerable<StructurePool> context) {
        context.register(BUILDINGS, buildings(context));
        context.register(STREETS, streets(context));
        context.register(VILLAGE_CENTER, villageCenter(context));
    }

    public static StructurePool buildings(Registerable<StructurePool> context) {
        RegistryEntryLookup<StructurePool> structureTemplatePoolSettings = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
        RegistryEntryLookup<StructureProcessorList> processorListSettings = context.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);

        List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> factories = List.of(
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/houses/small_village_house")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 10),
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/houses/slimy_profession_house")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 2),
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/houses/slimy_armorer_house")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 4),
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/houses/slimy_fletcher_house")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 4),
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/houses/slime_statue")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 1),
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/houses/slimy_farm")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 1)
        );

        return new StructurePool(
                structureTemplatePoolSettings.getOrThrow(StructurePools.EMPTY),
                factories,
                StructurePool.Projection.RIGID
        );
    }

    public static StructurePool streets(Registerable<StructurePool> context) {
        RegistryEntryLookup<StructurePool> structureTemplatePoolSettings = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
        RegistryEntryLookup<StructureProcessorList> processorListSettings = context.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);

        List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> factories = List.of(
                new Pair<>(projection -> EmptyPoolElement.INSTANCE, 3),
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/streets/straight_path")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 3),
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/streets/t_path")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 2),
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/streets/corner_path")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 2),
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/streets/cross_path")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 1)
        );

        return new StructurePool(
                structureTemplatePoolSettings.getOrThrow(StructurePools.EMPTY),
                factories,
                StructurePool.Projection.RIGID
        );
    }

    public static StructurePool villageCenter(Registerable<StructurePool> context) {
        RegistryEntryLookup<StructurePool> structureTemplatePoolSettings = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
        RegistryEntryLookup<StructureProcessorList> processorListSettings = context.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);

        List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> factories = List.of(
                new Pair<>(projection -> new SinglePoolElement(Either.left(Identifier.of("productiveslimes:slimy_village/village_center/village_center")), processorListSettings.getOrThrow(StructureProcessorLists.EMPTY), projection, Optional.empty()), 1)
        );

        return new StructurePool(
                structureTemplatePoolSettings.getOrThrow(StructurePools.EMPTY),
                factories,
                StructurePool.Projection.RIGID
        );
    }
}