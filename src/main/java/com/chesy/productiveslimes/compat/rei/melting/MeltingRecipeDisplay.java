package com.chesy.productiveslimes.compat.rei.melting;

import com.chesy.productiveslimes.recipe.MeltingRecipe;
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
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeltingRecipeDisplay extends BasicDisplay {
    private final int energy;
    private final int inputCount;

    public static final DisplaySerializer<MeltingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("ingredients").forGetter(MeltingRecipeDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("output").forGetter(MeltingRecipeDisplay::getOutputEntries),
                    Codec.INT.fieldOf("inputCount").forGetter(MeltingRecipeDisplay::getInputCount),
                    Codec.INT.fieldOf("energy").forGetter(MeltingRecipeDisplay::getEnergy)
            ).apply(instance, MeltingRecipeDisplay::new)),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    MeltingRecipeDisplay::getInputEntries,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    MeltingRecipeDisplay::getOutputEntries,
                    PacketCodecs.INTEGER,
                    MeltingRecipeDisplay::getInputCount,
                    PacketCodecs.INTEGER,
                    MeltingRecipeDisplay::getEnergy,
                    MeltingRecipeDisplay::new
            )
    );

    public MeltingRecipeDisplay(RecipeEntry<MeltingRecipe> recipe) {
        super(List.of(EntryIngredients.of(new ItemStack(recipe.value().getInputItems().getFirst().getMatchingItems().findFirst().get(), recipe.value().getInputCount())),
                        EntryIngredients.of(new ItemStack(Items.BUCKET, recipe.value().getOutputs().getFirst().getCount()))),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.value().getOutputs().getFirst()))));

        energy = recipe.value().getEnergy();
        inputCount = recipe.value().getInputCount();
    }

    public MeltingRecipeDisplay(List<EntryIngredient> input, List<EntryIngredient> output, int inputCount, int energy) {
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
        return MeltingCategory.MELTING;
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}
