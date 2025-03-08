package com.chesy.productiveslimes.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public final class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(null);
    public final Fluid fluid;
    private long amount;

    public static final Codec<Long> POSITIVE_LONG = longRangeWithMessage(1, Long.MAX_VALUE, p_274847_ -> "Value must be positive: " + p_274847_);

    private static Codec<Long> longRangeWithMessage(long min, long max, Function<Long, String> errorMessage) {
        return Codec.LONG
                .validate(
                        p_274889_ -> p_274889_.compareTo(min) >= 0 && p_274889_.compareTo(max) <= 0
                                ? DataResult.success(p_274889_)
                                : DataResult.error(() -> errorMessage.apply(p_274889_))
                );
    }

    public static final Codec<RegistryEntry<Fluid>> FLUID_NON_EMPTY_CODEC = Registries.FLUID.getEntryCodec().validate(holder -> {
        return holder.matches(Fluids.EMPTY.getRegistryEntry()) ? DataResult.error(() -> {
            return "Fluid must not be minecraft:empty";
        }) : DataResult.success(holder);
    });

    public static final Codec<FluidStack> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    instance -> instance.group(
                                    FLUID_NON_EMPTY_CODEC.fieldOf("id").forGetter(FluidStack::fluidHolder),
                                    POSITIVE_LONG.fieldOf("amount").forGetter(FluidStack::getAmount))
                            .apply(instance, FluidStack::new)));

    public static final PacketCodec<RegistryByteBuf, FluidStack> OPTIONAL_STREAM_CODEC = new PacketCodec<>() {
        private static final PacketCodec<RegistryByteBuf, RegistryEntry<Fluid>> FLUID_STREAM_CODEC = PacketCodecs.registryEntry(RegistryKeys.FLUID);

        @Override
        public FluidStack decode(RegistryByteBuf buf) {
            int amount = buf.readVarInt();
            if (amount <= 0) {
                return FluidStack.EMPTY;
            } else {
                RegistryEntry<Fluid> holder = FLUID_STREAM_CODEC.decode(buf);
                return new FluidStack(holder, amount);
            }
        }

        @Override
        public void encode(RegistryByteBuf buf, FluidStack stack) {
            if (stack.isEmpty()) {
                buf.writeVarLong(0);
            } else {
                buf.writeVarLong(stack.getAmount());
                FLUID_STREAM_CODEC.encode(buf, stack.fluidHolder());
            }
        }
    };

    public static final PacketCodec<RegistryByteBuf, FluidStack> STREAM_CODEC = new PacketCodec<>() {
        @Override
        public FluidStack decode(RegistryByteBuf buf) {
            FluidStack stack = FluidStack.OPTIONAL_STREAM_CODEC.decode(buf);
            if (stack.isEmpty()) {
                throw new DecoderException("Empty FluidStack not allowed");
            } else {
                return stack;
            }
        }

        @Override
        public void encode(RegistryByteBuf buf, FluidStack stack) {
            if (stack.isEmpty()) {
                throw new EncoderException("Empty FluidStack not allowed");
            } else {
                FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
            }
        }
    };

    private FluidStack(@Nullable Void unused) {
        this.fluid = null;
    }

    public FluidStack(RegistryEntry<Fluid> fluidHolder, long amount) {
        this(fluidHolder.value(), amount);
    }

    public FluidStack(Fluid fluid, long amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public FluidVariant getFluid() {
        return this.isEmpty() ? FluidVariant.of(Fluids.EMPTY) : FluidVariant.of(this.fluid);
    }

    public RegistryEntry<Fluid> fluidHolder() {
        return fluid.getRegistryEntry();
    }

    public boolean isEmpty() {
        return this == EMPTY || this.fluid == Fluids.EMPTY || this.amount <= 0;
    }

    public boolean is(TagKey<Fluid> tag) {
        return this.getFluid().getFluid().getRegistryEntry().isIn(tag);
    }

    public boolean is(Fluid fluid) {
        return this.getFluid().getFluid() == fluid;
    }

    public boolean is(Predicate<RegistryEntry<Fluid>> holderPredicate) {
        return holderPredicate.test(this.fluidHolder());
    }

    public boolean is(RegistryEntry<Fluid> holder) {
        return is(holder.value());
    }

    public boolean is(RegistryEntryList<Fluid> holderSet) {
        return holderSet.contains(this.fluidHolder());
    }

    public static boolean matches(FluidStack first, FluidStack second) {
        return first.is(second.getFluid().getFluid()) && first.getAmount() == second.getAmount();
    }

    public static boolean isSameFluid(FluidStack first, FluidStack second) {
        return first.is(second.getFluid().getFluid());
    }

    public String getDescriptionId() {
        return this.fluid.getDefaultState().getBlockState().getBlock().getTranslationKey();
    }

    public long getAmount() {
        return this.isEmpty() ? 0 : this.amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void grow(long addedAmount) {
        this.setAmount(this.getAmount() + addedAmount);
    }

    public void shrink(long removedAmount) {
        this.grow(-removedAmount);
    }

    public FluidStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new FluidStack(this.fluid, this.amount);
        }
    }
}
