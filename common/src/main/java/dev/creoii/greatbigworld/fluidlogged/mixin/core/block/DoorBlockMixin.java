package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin extends Block {
    public DoorBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gbw$registerDefaultFluidloggedProperty(BlockSetType type, BlockBehaviour.Properties properties, CallbackInfo ci) {
        registerDefaultState(defaultBlockState().setValue(Fluidloggable.FLUIDLOGGED, FluidType.EMPTY));
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void gbw$addFluidloggedProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(Fluidloggable.FLUIDLOGGED);
    }

    @Inject(method = "getStateForPlacement", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void gbw$fixDoorPlacementForWater(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state == null || !state.getProperties().contains(Fluidloggable.FLUIDLOGGED))
            return;
        cir.setReturnValue(state.setValue(Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.get(context.getLevel().getFluidState(context.getClickedPos()).getType())));
    }

    @Inject(method = "updateShape", at = @At("HEAD"))
    private void gbw$fixDoorNeighborUpdateForWater(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random, CallbackInfoReturnable<BlockState> cir) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType != FluidType.EMPTY) {
            ticks.scheduleTick(pos, fluidType.getFluid(), fluidType.getFluid().getTickDelay(level));
        }
    }

    @WrapOperation(method = "setPlacedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean gbw$fixDoorPlacementForWater(Level instance, BlockPos blockPos, BlockState state, Operation<Boolean> original) {
        return original.call(instance, blockPos, state.setValue(Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.get(instance.getFluidState(blockPos).getType())));
    }

    @Inject(method = "isPathfindable", at = @At("HEAD"), cancellable = true)
    private void gbw$fixNavigationForWaterGate(BlockState state, PathComputationType type, CallbackInfoReturnable<Boolean> cir) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType == FluidType.ICE) {
            cir.setReturnValue(true);
        } else if (fluidType == FluidType.WATER && type == PathComputationType.WATER) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift = At.Shift.AFTER))
    private void gbw$updateAfterNeighborChangeSetBlock(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston, CallbackInfo ci) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid()) {
            level.scheduleTick(pos, fluidType.getFluid(), fluidType.getFluid().getTickDelay(level));
        }
    }

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void gbw$disableUseWhenIcelogged(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
