package com.chesy.productiveslimes.tier;

/**
 * @param name                     Name of the tier
 * @param color                    Color of the tier (For items,block and entity)
 * @param mapColorId               Color on the map (ID reference to {@link net.minecraft.block.MapColor})
 * @param cooldown                 Cooldown for slime to drop slimeball (in ticks)
 * @param growthItemKey            Resource key of the item that can be used to grow the slime
 * @param solidingInputAmount      Fluid amount for soliding recipe to output 1 item
 * @param solidingOutputKey        Resource key of the output item for soliding recipe
 * @param synthesizingInputItemKey Resource key of the input item for synthesizing recipe
 * @param synthesizingInputDnaKey1 Resource key of the first input dna for synthesizing recipe
 * @param synthesizingInputDnaKey2 Resource key of the second input dna for synthesizing recipe
 * @param dnaOutputChance          Chance of output dna for synthesizing recipe
 */

public record ModTier(String name, int color, int mapColorId, int cooldown, String growthItemKey, int solidingInputAmount, String solidingOutputKey, String synthesizingInputItemKey, String synthesizingInputDnaKey1, String synthesizingInputDnaKey2, float dnaOutputChance)  {
    public ModTier {}
}
