package com.chesy.productiveslimes.block.entity.renderstate;

import com.chesy.productiveslimes.block.entity.SlimeSqueezerBlockEntity;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

import java.util.ArrayList;
import java.util.List;

public class SlimeSqueezerBlockEntityRenderState extends BlockEntityRenderState {
    public SlimeSqueezerBlockEntity blockEntity;
    public List<ItemRenderState> itemStackRenderStates = new ArrayList<>();
}
