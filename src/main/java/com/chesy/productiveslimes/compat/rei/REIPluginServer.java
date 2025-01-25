package com.chesy.productiveslimes.compat.rei;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.compat.rei.dna_extracting.DnaExtractingRecipeDisplay;
import com.chesy.productiveslimes.compat.rei.dna_synthesizing.DnaSynthesizingRecipeDisplay;
import com.chesy.productiveslimes.compat.rei.melting.MeltingRecipeDisplay;
import com.chesy.productiveslimes.compat.rei.soliding.SolidingRecipeDisplay;
import com.chesy.productiveslimes.recipe.*;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.minecraft.util.Identifier;

public class REIPluginServer implements REICommonPlugin {
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(Identifier.of(ProductiveSlimes.MODID, "melting"), MeltingRecipeDisplay.SERIALIZER);
        registry.register(Identifier.of(ProductiveSlimes.MODID, "soliding"), SolidingRecipeDisplay.SERIALIZER);
        registry.register(Identifier.of(ProductiveSlimes.MODID, "dna_extracting"), DnaExtractingRecipeDisplay.SERIALIZER);
        registry.register(Identifier.of(ProductiveSlimes.MODID, "dna_synthesizing"), DnaSynthesizingRecipeDisplay.SERIALIZER);
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(MeltingRecipe.class).filterType(ModRecipes.MELTING_TYPE).fill(MeltingRecipeDisplay::new);
        registry.beginRecipeFiller(SolidingRecipe.class).filterType(ModRecipes.SOLIDING_TYPE).fill(SolidingRecipeDisplay::new);
        registry.beginRecipeFiller(DnaExtractingRecipe.class).filterType(ModRecipes.DNA_EXTRACTING_TYPE).fill(DnaExtractingRecipeDisplay::new);
        registry.beginRecipeFiller(DnaSynthesizingRecipe.class).filterType(ModRecipes.DNA_SYNTHESIZING_TYPE).fill(DnaSynthesizingRecipeDisplay::new);
    }
}
