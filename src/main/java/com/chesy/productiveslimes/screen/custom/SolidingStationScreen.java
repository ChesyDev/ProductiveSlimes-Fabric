package com.chesy.productiveslimes.screen.custom;

import com.chesy.productiveslimes.ProductiveSlimes;
import com.chesy.productiveslimes.screen.renderer.FluidTankRenderer;
import com.chesy.productiveslimes.util.FluidStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SolidingStationScreen extends HandledScreen<SolidingStationMenu> {
    public static final Identifier GUI_TEXTURE =
            Identifier.of(ProductiveSlimes.MODID, "textures/gui/soliding_station_gui.png");

    public SolidingStationScreen(SolidingStationMenu handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.playerInventoryTitleY = 74;
        this.titleX = 54;
        this.titleY = 4;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderLayer::getGuiTextured, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
        int energyScaled = this.handler.getEnergyStoredScaled();

        context.drawTexture(RenderLayer::getGuiTextured, GUI_TEXTURE, x + 9, y + 13 + (57 - energyScaled), 176, 65 - energyScaled, 9, energyScaled, 256, 256);

        renderProgressArrow(context, x, y);

        FluidTankRenderer.renderFluidStack(context, new FluidStack(handler.blockEntity.getFluidTank().variant.getFluid(), handler.blockEntity.getFluidTank().getAmount()), handler.blockEntity.getFluidTank().getCapacity(), 15, 57, x + 22, y + 13);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if(handler.isCrafting()) {
            int k = handler.getScaledProgress();
            context.drawTexture(RenderLayer::getGuiTextured, GUI_TEXTURE, x + 94, y + 38, 176.0f, 0, k, 8, 256, 256);
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

        List<Text> fluidTankTooltip = new ArrayList<>();
        fluidTankTooltip.add(Text.translatable(handler.blockEntity.getFluidTank().variant.getFluid().getDefaultState().getBlockState().getBlock().getTranslationKey()));
        fluidTankTooltip.add(Text.translatable("productiveslimes.tooltip.liquid.amount.with.capacity", handler.blockEntity.getFluidTank().getAmount() / FluidConstants.BUCKET, handler.blockEntity.getFluidTank().getCapacity() / FluidConstants.BUCKET));
        if(isPointWithinBounds(22, 13, 15, 57, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, fluidTankTooltip, mouseX, mouseY);
        }
    }
}
