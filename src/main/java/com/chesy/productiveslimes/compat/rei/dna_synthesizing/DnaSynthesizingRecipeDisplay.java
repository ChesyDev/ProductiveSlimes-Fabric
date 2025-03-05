package com.chesy.productiveslimes.compat.rei.dna_synthesizing;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.recipe.DnaSynthesizingRecipe;
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

public class DnaSynthesizingRecipeDisplay extends BasicDisplay {
    public static final CategoryIdentifier<? extends DnaSynthesizingRecipeDisplay> CATEGORY = CategoryIdentifier.of(ProductiveSlimes.MODID, "dna_synthesizing");
    private final int energy;
    private final int inputCount;

    public static final DisplaySerializer<DnaSynthesizingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("ingredients").forGetter(DnaSynthesizingRecipeDisplay::getInputEntries),
                    EntryIngredient.codec().listOf().fieldOf("output").forGetter(DnaSynthesizingRecipeDisplay::getOutputEntries),
                    Codec.INT.fieldOf("inputCount").forGetter(DnaSynthesizingRecipeDisplay::getInputCount),
                    Codec.INT.fieldOf("energy").forGetter(DnaSynthesizingRecipeDisplay::getEnergy)
            ).apply(instance, DnaSynthesizingRecipeDisplay::new)),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    DnaSynthesizingRecipeDisplay::getInputEntries,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    DnaSynthesizingRecipeDisplay::getOutputEntries,
                    PacketCodecs.INTEGER,
                    DnaSynthesizingRecipeDisplay::getInputCount,
                    PacketCodecs.INTEGER,
                    DnaSynthesizingRecipeDisplay::getEnergy,
                    DnaSynthesizingRecipeDisplay::new
            )
    );

    public DnaSynthesizingRecipeDisplay(RecipeEntry<DnaSynthesizingRecipe> recipe) {
        super(
                List.of(
                        EntryIngredients.ofIngredient(recipe.value().inputItems().get(0).ingredient()),
                        EntryIngredients.ofIngredient(recipe.value().inputItems().get(1).ingredient()),
                        EntryIngredients.of(new ItemStack(recipe.value().inputItems().get(2).ingredient().getMatchingItems().toList().getFirst(), recipe.value().inputItems().get(2).count()))
                ),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.value().output().getFirst())))
        );

        energy = recipe.value().energy();
        inputCount = recipe.value().inputItems().get(2).count();
    }

    public DnaSynthesizingRecipeDisplay(List<EntryIngredient> input, List<EntryIngredient> output, int inputCount, int energy) {
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
        return CATEGORY;
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}
