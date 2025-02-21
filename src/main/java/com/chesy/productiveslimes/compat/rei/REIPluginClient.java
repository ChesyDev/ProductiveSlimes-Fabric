package com.chesy.productiveslimes.compat.rei;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.compat.rei.dna_extracting.DnaExtractingCategory;
import com.chesy.productiveslimes.compat.rei.dna_extracting.DnaExtractingRecipeDisplay;
import com.chesy.productiveslimes.compat.rei.dna_synthesizing.DnaSynthesizingCategory;
import com.chesy.productiveslimes.compat.rei.dna_synthesizing.DnaSynthesizingRecipeDisplay;
import com.chesy.productiveslimes.compat.rei.melting.MeltingCategory;
import com.chesy.productiveslimes.compat.rei.melting.MeltingRecipeDisplay;
import com.chesy.productiveslimes.compat.rei.soliding.SolidingCategory;
import com.chesy.productiveslimes.compat.rei.soliding.SolidingRecipeDisplay;
import com.chesy.productiveslimes.compat.rei.squeezing.SqueezingCategory;
import com.chesy.productiveslimes.compat.rei.squeezing.SqueezingRecipeDisplay;
import com.chesy.productiveslimes.recipe.*;
import com.chesy.productiveslimes.screen.custom.*;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class REIPluginClient implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new MeltingCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.MELTING_STATION)));
        registry.add(new SolidingCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.SOLIDING_STATION)));
        registry.add(new DnaExtractingCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.DNA_EXTRACTOR)));
        registry.add(new DnaSynthesizingCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.DNA_SYNTHESIZER)));
        registry.add(new SqueezingCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.SLIME_SQUEEZER)));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 77, ((screen.height - 166) / 2) + 38, 26, 8), MeltingStationScreen.class, MeltingCategory.MELTING);
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 77, ((screen.height - 166) / 2) + 38, 26, 8), SolidingStationScreen.class, SolidingCategory.SOLIDING);
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 77, ((screen.height - 166) / 2) + 38, 26, 8), DnaExtractorScreen.class, DnaExtractingCategory.DNA_EXTRACTING);
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 77, ((screen.height - 166) / 2) + 38, 26, 8), DnaSynthesizerScreen.class, DnaSynthesizingCategory.DNA_SYNTHESIZING);
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 77, ((screen.height - 166) / 2) + 38, 26, 8), SlimeSqueezerScreen.class, SqueezingCategory.SQUEEZING);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(MeltingRecipe.class, MeltingRecipe.Type.INSTANCE, MeltingRecipeDisplay::new);
        registry.registerRecipeFiller(SolidingRecipe.class, SolidingRecipe.Type.INSTANCE, SolidingRecipeDisplay::new);
        registry.registerRecipeFiller(DnaExtractingRecipe.class, DnaExtractingRecipe.Type.INSTANCE, DnaExtractingRecipeDisplay::new);
        registry.registerRecipeFiller(DnaSynthesizingRecipe.class, DnaSynthesizingRecipe.Type.INSTANCE, DnaSynthesizingRecipeDisplay::new);
        registry.registerRecipeFiller(SqueezingRecipe.class, SqueezingRecipe.Type.INSTANCE, SqueezingRecipeDisplay::new);
    }
}
