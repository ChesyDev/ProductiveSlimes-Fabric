package com.chesy.productiveslimes.compat.rei.dna_extracting;

import com.chesy.productiveslimes.ProductiveSlimes;
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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record DnaExtractingRecipeDisplay(RecipeEntry<DnaExtractingRecipe> recipe) implements Display {
    public static final CategoryIdentifier<? extends DnaExtractingRecipeDisplay> CATEGORY = CategoryIdentifier.of(ProductiveSlimes.MODID, "dna_extracting");

    public static final DisplaySerializer<DnaExtractingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("recipeId").forGetter(display -> display.recipe.id().getValue()),
                    DnaExtractingRecipe.Serializer.CODEC.fieldOf("ingredients").forGetter(display -> display.recipe.value())
            ).apply(instance, (identifier, dnaExtractingRecipe) -> new DnaExtractingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, identifier), dnaExtractingRecipe)))),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    display -> display.recipe.id().getValue(),
                    DnaExtractingRecipe.Serializer.STREAM_CODEC,
                    display -> display.recipe.value(),
                    (recipeId, dnaExtractingRecipe) -> new DnaExtractingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, recipeId), dnaExtractingRecipe))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(EntryIngredients.ofIngredient(recipe().value().inputItems()));
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        List<ItemStack> output = recipe().value().output();
        List<EntryIngredient> entries = new ArrayList<>();
        for (ItemStack itemStack : output) {
            entries.add(EntryIngredients.of(itemStack));
        }
        return entries;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CATEGORY;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(recipe.id().getValue());
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}
