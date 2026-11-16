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
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

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
        FenceGateBlock.class,
        BrewingStandBlock.class,

        AnvilBlock.class,
        AbstractBedBlock.class,
        ShulkerBoxBlock.class,
        BellBlock.class,
        CakeBlock.class,
        ComposterBlock.class,
        DaylightDetectorBlock.class,
        TrapDoorBlock.class,
        DoorBlock.class,
        EndPortalFrameBlock.class,
        GrindstoneBlock.class,
        HopperBlock.class,
        LecternBlock.class,
        PistonBaseBlock.class,
        PistonHeadBlock.class,
        StonecutterBlock.class,
        TurtleEggBlock.class,

        LadderBlock.class,
        ShelfBlock.class,
        CampfireBlock.class,
        DecoratedPotBlock.class,
        ScaffoldingBlock.class,
        SignBlock.class,
        MultifaceBlock.class,
        LanternBlock.class,
        AbstractChestBlock.class,
        CopperGolemStatueBlock.class,
        DragonEggBlock.class,
        EnchantingTableBlock.class
})
@Implements(@Interface(iface = Fluidloggable.class, prefix = "fluidlogged$"))
public abstract class CoreFluidloggedMixin implements OverlayState {
    public boolean canPlaceLiquid(@Nullable LivingEntity filler, BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return Fluidloggable.defaultCanPlaceLiquid(state);
    }

    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return Fluidloggable.defaultPlaceLiquid(world, pos, state, fluidState);
    }

    public ItemStack pickupBlock(@Nullable LivingEntity drainer, LevelAccessor world, BlockPos pos, BlockState state) {
        return Fluidloggable.defaultPickupBlock(world, pos, state);
    }

    public Optional<SoundEvent> getPickupSound() {
        return Fluidloggable.defaultGetPickupSound();
    }
}
