package com.chesy.productiveslimes.block.entity.renderstate;

import com.chesy.productiveslimes.block.entity.SlimeNestBlockEntity;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

public class SlimeNestBlockEntityRenderState extends BlockEntityRenderState {
    public SlimeNestBlockEntity blockEntity;
    public ItemRenderState itemStackRenderState;
    public int slimeColor;
}
