package com.chesy.productiveslimes.compat.rei.melting;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.recipe.MeltingRecipe;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record MeltingRecipeDisplay(RecipeEntry<MeltingRecipe> recipe) implements Display {
    public static final CategoryIdentifier<? extends MeltingRecipeDisplay> CATEGORY = CategoryIdentifier.of(ProductiveSlimes.MODID, "melting");

    public static final DisplaySerializer<MeltingRecipeDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("recipeId").forGetter(display -> display.recipe.id().getValue()),
                    MeltingRecipe.Serializer.CODEC.fieldOf("ingredients").forGetter(display -> display.recipe.value())
            ).apply(instance, (identifier, meltingRecipe) -> new MeltingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, identifier), meltingRecipe)))),
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC,
                    meltingRecipeDisplay -> meltingRecipeDisplay.recipe.id().getValue(),
                    MeltingRecipe.Serializer.STREAM_CODEC,
                    meltingRecipeDisplay -> meltingRecipeDisplay.recipe.value(),
                    (identifier, meltingRecipe) -> new MeltingRecipeDisplay(new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, identifier), meltingRecipe))
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> entryIngredients = new ArrayList<>();
        entryIngredients.add(EntryIngredients.of(new ItemStack(recipe.value().inputItems().ingredient().getMatchingItems().toList().getFirst(), recipe.value().inputItems().count())));
        entryIngredients.add(EntryIngredients.of(new ItemStack(Items.BUCKET, recipe.value().output().getFirst().getCount())));
        return entryIngredients;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.ofItemStacks(recipe.value().output()));
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
