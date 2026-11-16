package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({
        SpeleothemBlock.class,
        StairBlock.class,
        SlabBlock.class,
        WallBlock.class,
        AmethystClusterBlock.class,
        ChainBlock.class,
        LightningRodBlock.class,
        FenceBlock.class,
        IronBarsBlock.class,
        TrapDoorBlock.class,

        ShelfBlock.class,
        CampfireBlock.class,
        DecoratedPotBlock.class,
        ScaffoldingBlock.class,
        SignBlock.class,
        MultifaceBlock.class,
        LanternBlock.class,
        ChestBlock.class,
        EnderChestBlock.class,
        CopperGolemStatueBlock.class
})
public class CoreUpdateShapeFluidloggedMixin {
    @WrapOperation(method = "updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/ScheduledTickAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0))
    private Comparable<?> gbw$fixFluidloggableStateForNeighborUpdate(BlockState instance, Property<?> property, Operation<Comparable<?>> original) {
        return instance.getValue(Fluidloggable.FLUIDLOGGED) != FluidType.EMPTY;
    }

    @WrapOperation(method = "updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/ScheduledTickAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ScheduledTickAccess;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V", ordinal = 0))
    private void gbw$fixFluidloggableScheduleTick(ScheduledTickAccess instance, BlockPos pos, Fluid type, int tickDelay, Operation<Void> original, @Local(argsOnly = true, name = "state") BlockState blockState, @Local(argsOnly = true, name = "level") LevelReader levelReader) {
        FluidType fluidType = blockState.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid())
            original.call(instance, pos, fluidType.getFluid(), fluidType.getFluid().getTickDelay(levelReader));
    }
}
