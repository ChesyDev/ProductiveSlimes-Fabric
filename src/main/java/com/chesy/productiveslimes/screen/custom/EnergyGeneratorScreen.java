package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.util.ModIconButton;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EnergyGeneratorScreen extends HandledScreen<EnergyGeneratorMenu> {
    public static final Identifier GUI_TEXTURE =
            Identifier.of(ProductiveSlimes.MODID, "textures/gui/energy_generator_gui.png");

    public EnergyGeneratorScreen(EnergyGeneratorMenu handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        titleY = 5;
        titleX = 43;
        playerInventoryTitleY = 74;
        backgroundWidth = 217;

        int x = (width - 176) / 2;
        int y = (height - backgroundHeight) / 2;

        ButtonWidget iconButton = new ModIconButton(x + 155, y + 62, 16, 16, Text.translatable(""), 0, 0, 16, 0, button -> onButtonPress());

        this.addDrawableChild(iconButton);
    }

    private void onButtonPress(){
        switch (this.backgroundWidth) {
            case 217:
                this.backgroundWidth = 176;
                break;
            case 176:
                this.backgroundWidth = 217;
                break;
        }
        this.handler.toggleExtraSlots();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - 176) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0.0f, 0.0f, backgroundWidth, backgroundHeight, 256, 256);
        int energyScaled = this.handler.getEnergyStoredScaled();

        // Drawing the background texture using context.drawTexture
        context.drawTexture(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + 9, y + 13 + (57 - energyScaled), 232.0f, 57.0f - (float)energyScaled, 9, energyScaled, 256, 256);

        renderProgressArrow(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if(handler.isCrafting()) {
            int k = handler.getScaledProgress();
            context.drawTexture(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + 81, y + 47 + 14 - k, 218.0f, 14.0f - (float)k, 14, k, 256, 256);
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
