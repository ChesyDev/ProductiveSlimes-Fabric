package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SlimeballCollectorScreen extends HandledScreen<SlimeballCollectorMenu> {
    private static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/slimeball_collector_gui.png");

    public SlimeballCollectorScreen(SlimeballCollectorMenu handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.playerInventoryTitleY = 74;
        this.titleX = 49;
        this.titleY = 5;
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        this.addDrawableChild(new ButtonWidget.Builder(Text.literal("Toggle Area"), button -> {
            this.handler.inventory.setEnableOutline(this.handler.inventory.getData().get(0) == 0 ? 1 : 0);
        }).position(x + 88, y + 65).size(80, 16).build());
    }

    @Override
    protected void drawBackground(DrawContext pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        pGuiGraphics.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        drawMouseoverTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
