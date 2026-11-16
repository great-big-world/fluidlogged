package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin extends Block {
    public CakeBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gbw$registerDefaultFluidloggedProperty(Properties properties, CallbackInfo ci) {
        registerDefaultState(defaultBlockState().setValue(Fluidloggable.FLUIDLOGGED, FluidType.EMPTY));
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void gbw$addFluidloggedProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(Fluidloggable.FLUIDLOGGED);
    }

    @Inject(method = "updateShape", at = @At("HEAD"))
    private void gbw$fixUpdateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random, CallbackInfoReturnable<BlockState> cir) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid())
            ticks.scheduleTick(pos, fluidType.getFluid(), fluidType.getFluid().getTickDelay(level));
    }

    @Inject(method = "isPathfindable", at = @At("HEAD"), cancellable = true)
    private void gbw$fixPathfindableForIce(BlockState state, PathComputationType type, CallbackInfoReturnable<Boolean> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(true);
        }
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
