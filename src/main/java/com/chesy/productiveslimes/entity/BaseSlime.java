package com.chesy.productiveslimes.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BaseSlime extends SlimeEntity {
    private static final TrackedData<ItemStack> RESOURCE = DataTracker.registerData(BaseSlime.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Integer> ID_SIZE = DataTracker.registerData(BaseSlime.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> GROWTH_COUNTER = DataTracker.registerData(BaseSlime.class, TrackedDataHandlerRegistry.INTEGER);

    private final Item dropItem;
    private final Item growthItem;
    private final int color;
    private final int cooldown;
    private final EntityType<BaseSlime> entityType;

    public BaseSlime(EntityType<BaseSlime> entityType, World level, int cooldown, int color, Item dropItem, Item growthItem) {
        super(entityType, level);
        this.dropItem = dropItem;
        this.growthItem = growthItem;
        this.cooldown = cooldown;
        this.color = color;
        this.entityType = entityType;

        this.goalSelector.add(1, new SlimeFollowGoal(this, growthItem));
    }

    public int getColor() {
        return color;
    }

    public int getCooldown() {
        return cooldown;
    }

    public ItemStack getDropItem() {
        return dropItem.getDefaultStack();
    }

    public ItemStack getGrowthItem() {
        return growthItem.getDefaultStack();
    }

    public EntityType<BaseSlime> getEntityType() {
        return entityType;
    }

    @Override
    public Text getName() {
        return super.getName();
    }

    @Nullable
    @Override
    public Text getCustomName() {
        return super.getCustomName();
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(RESOURCE, ItemStack.EMPTY);
        builder.add(ID_SIZE, 1);
        builder.add(GROWTH_COUNTER, 0);
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
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.setRemoved(reason);
        if (reason == Entity.RemovalReason.KILLED) {
            this.emitGameEvent(GameEvent.ENTITY_DIE);

            if (this.getSize() == 1) {
                this.dropResource();
            }
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            if (player.isSneaking()) {
                if (!getWorld().isClient()) {
                    if (player.getStackInHand(hand).getItem() == growthItem && this.getSize() < 4 && player.getStackInHand(hand).getCount() > this.getSize()) {
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

    public static TrackedData<Integer> getGrowthCounter() {
        return GROWTH_COUNTER;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(ID_SIZE, nbt.getInt("size"));
        this.dataTracker.set(GROWTH_COUNTER, nbt.getInt("growth_counter"));

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("growth_counter", this.dataTracker.get(GROWTH_COUNTER));
        nbt.putInt("size", this.dataTracker.get(ID_SIZE));
    }

    public int getNextDropTime() {
        return cooldown - this.dataTracker.get(GROWTH_COUNTER);
    }

    public void setResource(ItemStack stack) {
        this.dataTracker.set(RESOURCE, stack);
        resetGrowthCount();
    }

    public ItemStack getResourceItem() {
        if (!this.dataTracker.get(RESOURCE).isEmpty()) {
            return this.dataTracker.get(RESOURCE);
        }

        return ItemStack.EMPTY;
    }

    public void dropResource() {
        ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), new ItemStack(this.dropItem, this.getSize()));
        this.getWorld().spawnEntity(itemEntity);
    }

    @Override
    public void tick() {
        super.tick();
        if (!isDead()) {
            countGrowth();

            if (readyForNewResource()) {
                dropResource();
                resetGrowthCount();
            }
        }
    }

    private boolean readyForNewResource() {
        return this.dataTracker.get(GROWTH_COUNTER) >= cooldown;
    }

    private void resetGrowthCount() {
        this.dataTracker.set(GROWTH_COUNTER, 0);
    }

    private void countGrowth() {
        this.dataTracker.set(GROWTH_COUNTER, this.dataTracker.get(GROWTH_COUNTER) + 1);
    }

    @Override
    public int getSize() {
        return this.dataTracker.get(ID_SIZE);
    }

    @Override
    public void setSize(int pSize, boolean pResetHealth) {
        // Setting the size based on the number of resources
        // int newSize = this.entityData.get(RESOURCE).getCount() * 2 - 1; // INSANE GROWTH (64 -> Size 127)
        int i = Math.clamp(pSize, 1, 127);
        this.dataTracker.set(ID_SIZE, i);
        this.refreshPosition();
        this.calculateDimensions();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue((double) (i * i));
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue((double) (0.2F + 0.1F * (float) i));
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue((double) i);

        this.experiencePoints = i;
    }


    @Override
    protected boolean isDisallowedInPeaceful() {
        return false;
    }

    @Override
    protected boolean canAttack() {
        return false;
    }

    protected int getTicksUntilNextJump() {
        return this.random.nextInt(20) + 10;
    }

    static class SlimeFollowGoal extends Goal {
        private final SlimeEntity slime;
        private int growTiredTimer;
        private final Item targetItem; // The item to check for
        public SlimeFollowGoal(SlimeEntity slime, Item targetItem) {
            this.slime = slime;
            this.targetItem = targetItem;
            this.setControls(EnumSet.of(Control.LOOK));
        }
        private boolean isPlayerHoldingTargetItem(PlayerEntity player) {
            return player.getMainHandStack().isOf(targetItem) || player.getOffHandStack().isOf(targetItem);
        }
        private boolean isInRange(PlayerEntity player) {
            return this.slime.distanceTo(player) <= 8.0F;
        }
        private PlayerEntity findNearestPlayerWithItem() {
            return getServerWorld(this.slime).getClosestPlayer(
                    TargetPredicate.createNonAttackable().setPredicate((livingEntity, level) -> {
                        if (livingEntity instanceof PlayerEntity player) {
                            return isPlayerHoldingTargetItem(player) && this.slime.getSize() < 4 && isInRange(player);
                        }
                        return false;
                    }),
                    this.slime.getX(),
                    this.slime.getY(),
                    this.slime.getZ()
            );
        }
        @Override
        public boolean canStart() {
            PlayerEntity player = findNearestPlayerWithItem();
            if (player == null) {
                return false;
            }
            this.slime.setTarget(player);
            return this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
        }
        @Override
        public void start() {
            this.growTiredTimer = getTickCount(300);
            super.start();
        }
        @Override
        public boolean shouldContinue() {
            PlayerEntity player = findNearestPlayerWithItem();
            if (player == null) {
                return false;
            }
            this.slime.setTarget(player);
            return --this.growTiredTimer > 0;
        }
        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            PlayerEntity player = findNearestPlayerWithItem();
            if (player != null) {
                this.slime.lookAtEntity(player, 10.0F, 10.0F);
            }
            if (this.slime.getMoveControl() instanceof BaseSlime.SlimeMoveControl slimeMoveControl) {
                slimeMoveControl.look(this.slime.getYaw(), false);
            }
        }
    }
}