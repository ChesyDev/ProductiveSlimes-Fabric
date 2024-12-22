package com.chesy.productiveslimes.screen;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.screen.custom.GuidebookMenuHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModMenuTypes {
    public static final ScreenHandlerType<GuidebookMenuHandler> GUIDEBOOK_MENU_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ProductiveSlimes.MOD_ID, "guidebook_menu_handler"),
                    new ExtendedScreenHandlerType<>(GuidebookMenuHandler::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        ProductiveSlimes.LOGGER.info("Registering Screen Handlers for " + ProductiveSlimes.MOD_ID);
    }
}
