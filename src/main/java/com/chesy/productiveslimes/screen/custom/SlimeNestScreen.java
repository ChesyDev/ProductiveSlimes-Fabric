package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

public class SlimeNestScreen extends HandledScreen<SlimeNestMenu> {
    private static final Identifier TEXTURE = Identifier.of(ProductiveSlimes.MODID, "textures/gui/slime_nest_gui.png");

    public SlimeNestScreen(SlimeNestMenu pMenu, PlayerInventory pPlayerInventory, Text pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.playerInventoryTitleY = 100000;
        this.titleX = 35;
        this.titleY = 5;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int countdown = handler.getCountdown();
        Text cd = Text.translatable("slimenest.productiveslimes.cooldown", countdown).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xa5f5a6)));
        Text size = Text.translatable("slimenest.productiveslimes.slime_size", handler.getSlimeSize()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xa5f5a6)));
        Text multiplier = Text.translatable("slimenest.productiveslimes.multiplier", handler.getMultiplier()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xa5f5a6)));
        Text dropItem = Text.translatable("slimenest.productiveslimes.drop_item").append(Text.translatable(handler.getDrop().getItem().getTranslationKey())).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xa5f5a6)));

        int guiLeft = (width - backgroundWidth) / 2;
        int guiTop = (height - backgroundHeight) / 2;

        int textX = guiLeft + 54;
        int textY = guiTop + 17;

        if (!(handler.hasSlime() && handler.hasOutputSlot())){
            if(!handler.hasOutputSlot()){
                cd = Text.translatable("slimenest.productiveslimes.no_output_slot").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xd59c20)));
            }
            else {
                cd = Text.translatable("slimenest.productiveslimes.no_slime_found").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xc70d0d)));
            }
        }

        pGuiGraphics.getMatrices().pushMatrix();
        pGuiGraphics.getMatrices().translate(textX, textY);
        pGuiGraphics.getMatrices().scale(0.75f, 0.75f);
        pGuiGraphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, cd, 0,  0, 0xFFFFFFFF);
        pGuiGraphics.getMatrices().popMatrix();

        if (handler.hasSlime()){
            pGuiGraphics.getMatrices().pushMatrix();
            pGuiGraphics.getMatrices().translate(textX, textY + 8);
            pGuiGraphics.getMatrices().scale(0.75f, 0.75f);
            pGuiGraphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, size, 0,  0, 0xFFFFFFFF);
            pGuiGraphics.getMatrices().popMatrix();
        }

        if (handler.hasSlime()){
            pGuiGraphics.getMatrices().pushMatrix();
            if (String.valueOf(handler.getMultiplier()).length() >= 6){
                pGuiGraphics.getMatrices().translate(textX, textY + 16);
                pGuiGraphics.getMatrices().scale(0.7f, 0.7f);
                pGuiGraphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, multiplier, 0,  0, 0xFFFFFFFF);
            }
            else{
                pGuiGraphics.getMatrices().translate(textX, textY + 16);
                pGuiGraphics.getMatrices().scale(0.75f, 0.75f);
                pGuiGraphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, multiplier, 0,  0, 0xFFFFFFFF);
            }
            pGuiGraphics.getMatrices().popMatrix();

            pGuiGraphics.getMatrices().pushMatrix();
            pGuiGraphics.getMatrices().translate(textX, textY + 24);
            pGuiGraphics.getMatrices().scale(0.75f, 0.75f);
            pGuiGraphics.drawWrappedTextWithShadow(MinecraftClient.getInstance().textRenderer, dropItem, 0,  0, 85,  0xFFFFFFFF);
            pGuiGraphics.getMatrices().popMatrix();
        }
        drawMouseoverTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
