package com.chesy.productiveslimes.screen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.screen.custom.*;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModMenuTypes {
    public static final ScreenHandlerType<GuidebookMenu> GUIDEBOOK_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MODID, "guidebook_menu_handler"),
                    new ExtendedScreenHandlerType<>(GuidebookMenu::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<MeltingStationMenu> MELTING_STATION_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MODID, "melting_station_menu_handler"),
                    new ExtendedScreenHandlerType<>(MeltingStationMenu::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<EnergyGeneratorMenu> ENERGY_GENERATOR_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MODID, "energy_generator_menu_handler"),
                    new ExtendedScreenHandlerType<>(EnergyGeneratorMenu::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<SolidingStationMenu> SOLIDING_STATION_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MODID, "soliding_station_menu_handler"),
                    new ExtendedScreenHandlerType<>(SolidingStationMenu::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<DnaExtractorMenu> DNA_EXTRACTOR_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MODID, "dna_extractor_menu_handler"),
                    new ExtendedScreenHandlerType<>(DnaExtractorMenu::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<DnaSynthesizerMenu> DNA_SYNTHESIZER_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MODID, "dna_synthesizer_menu_handler"),
                    new ExtendedScreenHandlerType<>(DnaSynthesizerMenu::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<SlimeSqueezerMenu> SLIME_SQUEEZER_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MODID, "slime_squeezer_menu_handler"),
                    new ExtendedScreenHandlerType<>(SlimeSqueezerMenu::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        ProductiveSlimes.LOGGER.info("Registering Screen Handlers for " + ProductiveSlimes.MODID);
    }
}
