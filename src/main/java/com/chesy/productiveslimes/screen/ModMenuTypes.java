package com.chesy.productiveslimes.screen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.screen.custom.EnergyGeneratorMenu;
import com.chesy.productiveslimes.screen.custom.GuidebookMenu;
import com.chesy.productiveslimes.screen.custom.MeltingStationMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModMenuTypes {
    public static final ScreenHandlerType<GuidebookMenu> GUIDEBOOK_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MOD_ID, "guidebook_menu_handler"),
                    new ExtendedScreenHandlerType<>(GuidebookMenu::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<MeltingStationMenu> MELTING_STATION_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MOD_ID, "melting_station_menu_handler"),
                    new ExtendedScreenHandlerType<>(MeltingStationMenu::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<EnergyGeneratorMenu> ENERGY_GENERATOR_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MOD_ID, "energy_generator_menu_handler"),
                    new ExtendedScreenHandlerType<>(EnergyGeneratorMenu::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        ProductiveSlimes.LOGGER.info("Registering Screen Handlers for " + ProductiveSlimes.MOD_ID);
    }
}
