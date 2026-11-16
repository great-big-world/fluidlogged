package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantingTableBlockMixin extends BaseEntityBlock {
    protected EnchantingTableBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gbw$registerDefaultFluidloggedProperty(BlockBehaviour.Properties properties, CallbackInfo ci) {
        registerDefaultState(defaultBlockState().setValue(Fluidloggable.FLUIDLOGGED, FluidType.EMPTY));
    }

    @Inject(method = "isPathfindable", at = @At("HEAD"), cancellable = true)
    private void gbw$fixPathfindableForIce(BlockState state, PathComputationType type, CallbackInfoReturnable<Boolean> cir) {
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(Fluidloggable.FLUIDLOGGED);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid())
            ticks.scheduleTick(pos, fluidType.getFluid(), fluidType.getFluid().getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
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
