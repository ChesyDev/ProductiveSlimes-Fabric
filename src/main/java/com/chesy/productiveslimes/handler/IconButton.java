package com.chesy.productiveslimes.handler;

import com.chesy.productiveslimes.ProductiveSlimes;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.Component;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButton extends ButtonWidget {
    public static final Identifier iconTexture = Identifier.of(ProductiveSlimes.MOD_ID, "textures/gui/widgets.png");
    private final int closedTextureX;
    private final int closedTextureY;
    private final int openTextureX;
    private final int openTextureY;
    private boolean isOpen;

    public IconButton(int x, int y, int width, int height, Text message, int closedTextureX, int closedTextureY, int openTextureX, int openTextureY, PressAction onPress) {
        super(x, y, width, height, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.closedTextureX = closedTextureX;
        this.closedTextureY = closedTextureY;
        this.openTextureX = openTextureX;
        this.openTextureY = openTextureY;
        this.isOpen = true;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int textureX = isOpen ? closedTextureX : openTextureX;
        int textureY = isOpen ? closedTextureY : openTextureY;
        context.drawTexture(RenderLayer::getGuiTextured, iconTexture, this.getX(), this.getY(), textureX, textureY, this.width, this.height, 256, 256);

        if (this.isHovered()) {
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x80FFFFFF);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.isOpen = !this.isOpen;
        this.onPress.onPress(this);
    }
}
