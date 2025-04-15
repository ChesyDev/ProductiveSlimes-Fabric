package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DnaSynthesizerScreen extends HandledScreen<DnaSynthesizerMenu> {
    private static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/dna_synthesizer_gui.png");

    public DnaSynthesizerScreen(DnaSynthesizerMenu handler, PlayerInventory inventory, Text title) {
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
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        pGuiGraphics.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        renderDnaBar(pGuiGraphics, x, y);
        renderEnergyBar(pGuiGraphics, x, y);
        renderProgressArrow(pGuiGraphics, x, y);
    }

    private void renderDnaBar(DrawContext guiGraphics, int x, int y) {
        if(handler.isCrafting()) {
            guiGraphics.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 36, y + 30, 176, 66, 6, handler.getDnaProgress(), 256, 256);
        }
    }

    private void renderEnergyBar(DrawContext guiGraphics, int x, int y) {
        int energyScaled = this.handler.getEnergyStoredScaled();

        guiGraphics.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 9, y + 13 + (57 - energyScaled), 176, 65 - energyScaled, 9, energyScaled, 256, 256);
    }

    private void renderProgressArrow(DrawContext guiGraphics, int x, int y) {
        if(handler.isCrafting()) {
            guiGraphics.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 77, y + 38, 176, 0, handler.getScaledProgress(), 8, 256, 256);
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
