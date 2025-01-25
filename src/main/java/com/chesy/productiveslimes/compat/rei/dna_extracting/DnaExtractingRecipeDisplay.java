package com.chesy.productiveslimes.compat.rei.dna_extracting;

import com.chesy.productiveslimes.recipe.DnaExtractingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DnaExtractingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final float outputChance;
    private final int inputCount;

    public static final DisplaySerializer<DnaExtractingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("ingredients").forGetter(DnaExtractingRecipeDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("output").forGetter(DnaExtractingRecipeDisplay::getOutputEntries),
                    Codec.INT.fieldOf("inputCount").forGetter(DnaExtractingRecipeDisplay::getInputCount),
                    Codec.INT.fieldOf("energy").forGetter(DnaExtractingRecipeDisplay::getEnergy),
                    Codec.FLOAT.fieldOf("output_chance").forGetter(DnaExtractingRecipeDisplay::getOutputChance)
            ).apply(instance, DnaExtractingRecipeDisplay::new)),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    DnaExtractingRecipeDisplay::getInputEntries,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    DnaExtractingRecipeDisplay::getOutputEntries,
                    PacketCodecs.INTEGER,
                    DnaExtractingRecipeDisplay::getInputCount,
                    PacketCodecs.INTEGER,
                    DnaExtractingRecipeDisplay::getEnergy,
                    PacketCodecs.FLOAT,
                    DnaExtractingRecipeDisplay::getOutputChance,
                    DnaExtractingRecipeDisplay::new
            )
    );

    public DnaExtractingRecipeDisplay(RecipeEntry<DnaExtractingRecipe> recipe) {
        super(List.of(EntryIngredients.ofIngredient(recipe.value().inputItems().getFirst())),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.value().output().get(0))),
                        EntryIngredient.of(EntryStacks.of(recipe.value().output().size() > 1 ? recipe.value().output().get(1) : ItemStack.EMPTY))));
        energy = recipe.value().energy();
        outputChance = recipe.value().outputChance();
        inputCount = recipe.value().inputCount();
    }

    public DnaExtractingRecipeDisplay(List<EntryIngredient> input, List<EntryIngredient> output, int inputCount, int energy, float outputChance) {
        super(input, output);
        this.energy = energy;
        this.outputChance = outputChance;
        this.inputCount = inputCount;
    }

    public int getEnergy() {
        return energy;
    }

    public float getOutputChance() {
        return outputChance;
    }

    public int getInputCount() {
        return inputCount;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return DnaExtractingCategory.DNA_EXTRACTING;
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}