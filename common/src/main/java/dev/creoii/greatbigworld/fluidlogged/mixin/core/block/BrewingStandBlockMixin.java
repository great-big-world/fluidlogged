package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlock.class)
public abstract class BrewingStandBlockMixin extends BaseEntityBlock {
    protected BrewingStandBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gbw$registerDefaultFluidloggedProperty(Properties properties, CallbackInfo ci) {
        registerDefaultState(defaultBlockState().setValue(dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable.FLUIDLOGGED, FluidType.EMPTY));
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void gbw$addFluidloggedProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable.FLUIDLOGGED);
    }

    @Inject(method = "isPathfindable", at = @At("HEAD"), cancellable = true)
    private void gbw$fixNavigationForWaterGate(BlockState state, PathComputationType type, CallbackInfoReturnable<Boolean> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void gbw$disableUseWhenIcelogged(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid()) {
            ticks.scheduleTick(pos, fluidType.getFluid(), fluidType.getFluid().getTickDelay(level));
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.get(context.getLevel().getFluidState(context.getClickedPos()).getType()));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid() && fluidType.getFluid() instanceof FlowingFluid flowableFluid) {
            return flowableFluid.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }
}
