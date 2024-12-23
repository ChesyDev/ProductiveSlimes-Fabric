package com.chesy.productiveslimes.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BaseSlime extends SlimeEntity {
    private final Item item;
    private final Item growthItem;
    public BaseSlime(EntityType<? extends SlimeEntity> entityType, World level, int cooldown, int color, Item dropItem, Item growthItem) {
        super(entityType, level);
        this.item = dropItem;
        this.growthItem = growthItem;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.MOVEMENT_SPEED, 0.2D)
                .add(EntityAttributes.ATTACK_DAMAGE, 0)
                .add(EntityAttributes.FOLLOW_RANGE, 16.0D)
                .add(EntityAttributes.MAX_HEALTH, 4.0D);
    }

    @Nullable
    @Override
    public EntityAttributeInstance getAttributeInstance(RegistryEntry<EntityAttribute> attribute) {
        return createAttributes().build().createOverride(EntityAttributeInstance::clearModifiers, attribute);
    }

    public void growthSlime(PlayerEntity player, Hand hand, BaseSlime slime) {
        slime.setSize(slime.getSize() + 1, false);
        slime.setHealth(slime.getMaxHealth());
        slime.setPos(slime.getX(), slime.getY() + 1, slime.getZ());
        player.getStackInHand(hand).decrement(slime.getSize() + 1);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(hand == Hand.MAIN_HAND) {
            if(player.isSneaking()){
                if(!getWorld().isClient()){
                    if(player.getStackInHand(hand).getItem() == growthItem && this.getSize() < 4 && player.getStackInHand(hand).getCount() > this.getSize()) {
                        this.growthSlime(player, hand, this);
                    }
                }
            }
        }

        return super.interactMob(player, hand);
    }

    @Override
    public AttributeContainer getAttributes() {
        return new AttributeContainer(createAttributes().build());
    }

    @Override
    public double getAttributeBaseValue(RegistryEntry<EntityAttribute> attribute) {
        return createAttributes().build().getBaseValue(attribute);
    }
}