package com.chesy.productiveslimes.compat.rei.soliding;

import com.chesy.productiveslimes.recipe.SolidingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolidingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final int inputCount;

    public static final DisplaySerializer<SolidingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("ingredients").forGetter(SolidingRecipeDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("output").forGetter(SolidingRecipeDisplay::getOutputEntries),
                    Codec.INT.fieldOf("inputCount").forGetter(SolidingRecipeDisplay::getInputCount),
                    Codec.INT.fieldOf("energy").forGetter(SolidingRecipeDisplay::getEnergy)
            ).apply(instance, SolidingRecipeDisplay::new)),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    SolidingRecipeDisplay::getInputEntries,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    SolidingRecipeDisplay::getOutputEntries,
                    PacketCodecs.INTEGER,
                    SolidingRecipeDisplay::getInputCount,
                    PacketCodecs.INTEGER,
                    SolidingRecipeDisplay::getEnergy,
                    SolidingRecipeDisplay::new
            )
    );

    public SolidingRecipeDisplay(RecipeEntry<SolidingRecipe> recipe) {
        super(
                List.of(EntryIngredients.ofIngredient(recipe.value().getInputItems().getFirst())),
                List.of(
                        EntryIngredient.of(EntryStacks.of(recipe.value().getOutputs().get(0))),
                        EntryIngredient.of(EntryStacks.of(recipe.value().getOutputs().get(1)))
                )
        );

        energy = recipe.value().getEnergy();
        inputCount = recipe.value().getInputCount();
    }

    public SolidingRecipeDisplay(List<EntryIngredient> input, List<EntryIngredient> output, int inputCount, int energy) {
        super(input, output);
        this.energy = energy;
        this.inputCount = inputCount;
    }

    public int getEnergy() {
        return energy;
    }

    public int getInputCount() {
        return inputCount;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SolidingCategory.SOLIDING;
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}