package com.chesy.productiveslimes.worldgen.structure;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;

public class ModStructureSets {
    public static final RegistryKey<StructureSet> SLIMY_VILLAGE = RegistryKey.of(RegistryKeys.STRUCTURE_SET, Identifier.of(ProductiveSlimes.MODID, "slimy_village"));

    public static void bootstrap(Registerable<StructureSet> context){
        context.register(SLIMY_VILLAGE, slimyVillage(context));
    }

    public static StructureSet slimyVillage(Registerable<StructureSet> context){
        RegistryEntryLookup<Structure> structureSettings = context.getRegistryLookup(RegistryKeys.STRUCTURE);

        return new StructureSet(
                List.of(
                        new StructureSet.WeightedEntry(structureSettings.getOrThrow(ModStructures.SLIMY_VILLAGE), 1)
                ),
                new RandomSpreadStructurePlacement(34, 8, SpreadType.TRIANGULAR, 19011220)
        );
    }
}
