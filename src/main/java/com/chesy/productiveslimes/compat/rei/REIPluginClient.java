package com.chesy.productiveslimes.compat.rei;

import com.chesy.productiveslimes.block.ModBlocks;
import com.chesy.productiveslimes.compat.rei.melting.MeltingCategory;
import com.chesy.productiveslimes.compat.rei.soliding.SolidingCategory;
import com.chesy.productiveslimes.screen.custom.MeltingStationScreen;
import com.chesy.productiveslimes.screen.custom.SolidingStationScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class REIPluginClient implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new MeltingCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.MELTING_STATION)));
        registry.add(new SolidingCategory(), configuration -> configuration.addWorkstations(EntryStacks.of(ModBlocks.SOLIDING_STATION)));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 77, ((screen.height - 166) / 2) + 38, 26, 8), MeltingStationScreen.class, MeltingCategory.MELTING);
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 77, ((screen.height - 166) / 2) + 38, 26, 8), SolidingStationScreen.class, SolidingCategory.SOLIDING);
    }
}
