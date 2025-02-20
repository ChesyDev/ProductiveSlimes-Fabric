package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SlimeSqueezerScreen extends HandledScreen<SlimeSqueezerMenu> {
    private static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/slime_squeezer_gui.png");

    public SlimeSqueezerScreen(SlimeSqueezerMenu handler, PlayerInventory inventory, Text title) {
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
    protected void drawBackground(DrawContext pGuiGraphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        pGuiGraphics.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        int energyScaled = this.handler.getEnergyStoredScaled();
        pGuiGraphics.drawTexture(TEXTURE, x + 9, y + 13 + (57 - energyScaled), 176, 65 - energyScaled, 9, energyScaled, 256, 256);
        renderProgressArrow(pGuiGraphics, x, y);
    }

    private void renderProgressArrow(DrawContext guiGraphics, int x, int y) {
        if (handler.isCrafting()) {
            guiGraphics.drawTexture(TEXTURE, x + 77, y + 38, 176, 0, handler.getScaledProgress(), 8, 256, 256);
        }
    }

    @Override
    public void render(DrawContext pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        drawMouseoverTooltip(pGuiGraphics, pMouseX, pMouseY);

        int energyStored = this.handler.getEnergy();
        int maxEnergy = this.handler.getMaxEnergy();

        Text text = Text.translatable("gui.productiveslimes.energy_stored", energyStored, maxEnergy);
        if (isPointWithinBounds(9, 13, 9, 57, pMouseX, pMouseY)) {
            pGuiGraphics.drawTooltip(this.textRenderer, text, pMouseX, pMouseY);
        }
    }
}
