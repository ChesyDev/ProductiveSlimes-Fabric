package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MeltingStationScreen extends HandledScreen<MeltingStationMenu> {
    public static final Identifier GUI_TEXTURE =
            Identifier.of(ProductiveSlimes.MODID, "textures/gui/melting_station_gui.png");

    public MeltingStationScreen(MeltingStationMenu handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.playerInventoryTitleY = 74;
        this.titleX = 54;
        this.titleY = 5;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
        int energyScaled = this.handler.getEnergyStoredScaled();

        context.drawTexture(GUI_TEXTURE, x + 9, y + 13 + (57 - energyScaled), 176, 65 - energyScaled, 9, energyScaled, 256, 256);

        renderProgressArrow(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if(handler.isCrafting()) {
            int k = handler.getScaledProgress();
            context.drawTexture(GUI_TEXTURE, x + 77, y + 38, 176.0f, 0, k, 8, 256, 256);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        int energyStored = this.handler.getEnergy();
        int maxEnergy = this.handler.getMaxEnergy();

        Text text = Text.translatable("gui.productiveslimes.energy_stored", energyStored, maxEnergy);
        if(isPointWithinBounds(9, 13, 9, 57, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, text, mouseX, mouseY);
        }
    }
}
