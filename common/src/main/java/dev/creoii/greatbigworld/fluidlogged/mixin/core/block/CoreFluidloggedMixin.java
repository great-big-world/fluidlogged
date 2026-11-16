package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import dev.creoii.greatbigworld.fluidlogged.util.OverlayState;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin({
        SpeleothemBlock.class,
        SlabBlock.class,
        StairBlock.class,
        WallBlock.class,
        AmethystClusterBlock.class,
        ChainBlock.class,
        RodBlock.class,
        CrossCollisionBlock.class,
        FenceGateBlock.class
})
@Implements(@Interface(iface = Fluidloggable.class, prefix = "fluidlogged$"))
public abstract class CoreFluidloggedMixin implements OverlayState {
    @Unique
    public boolean canPlaceLiquid(@Nullable LivingEntity filler, BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return Fluidloggable.defaultCanPlaceLiquid(state);
    }

    @Unique
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return Fluidloggable.defaultPlaceLiquid(world, pos, state, fluidState);
    }

    @Unique
    public ItemStack pickupBlock(@Nullable LivingEntity drainer, LevelAccessor world, BlockPos pos, BlockState state) {
        return Fluidloggable.defaultPickupBlock(world, pos, state);
    }

    @Unique
    public Optional<SoundEvent> getPickupSound() {
        return Fluidloggable.defaultGetPickupSound();
    }
}
