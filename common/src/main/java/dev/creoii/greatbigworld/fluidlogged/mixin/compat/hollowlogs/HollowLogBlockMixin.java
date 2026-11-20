package dev.creoii.greatbigworld.fluidlogged.mixin.compat.hollowlogs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import dev.creoii.greatbigworld.fluidlogged.util.OverlayState;
import dev.creoii.greatbigworld.hollowlogs.block.HollowLogBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HollowLogBlock.class)
public abstract class HollowLogBlockMixin extends RotatedPillarBlock implements Fluidloggable, OverlayState {
    public HollowLogBlockMixin(Properties properties) {
        super(properties);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    private Object gbw$setFluidloggableDefaultState(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original) {
        if (property == BlockStateProperties.WATERLOGGED)
            return original.call(instance, Fluidloggable.FLUIDLOGGED, FluidType.EMPTY);
        return original.call(instance, property, comparable);
    }

    @WrapOperation(method = "createBlockStateDefinition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"))
    private StateDefinition.Builder<Block, BlockState> gbw$addFluidloggableProperty(StateDefinition.Builder<Block, BlockState> instance, Property<?>[] properties, Operation<StateDefinition.Builder<Block, BlockState>> original) {
        return original.call(instance, Fluidloggable.replaceWaterloggedWithFluidlogged(properties));
    }

    @WrapOperation(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 1))
    private Object gbw$fixFluidloggablePlacementState(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original, @Local(argsOnly = true, name = "ctx") BlockPlaceContext ctx) {
        return original.call(instance, Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.get(ctx.getLevel().getFluidState(ctx.getClickedPos()).getType()));
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void gbw$injectFullShapeForIce(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(Shapes.block());
        }
    }

    @WrapOperation(method = "getFluidState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0))
    private Comparable<?> gbw$fixFluidloggableFluidState(BlockState instance, Property<?> property, Operation<Comparable<?>> original) {
        return instance.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.WATER;
    }

    @WrapOperation(method = "getFluidState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RotatedPillarBlock;getFluidState(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/material/FluidState;"))
    private FluidState gbw$fixFluidloggableFluidState(HollowLogBlock instance, BlockState state, Operation<FluidState> original) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid() && fluidType.getFluid() instanceof FlowingFluid flowableFluid) {
            return flowableFluid.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected boolean skipRendering(final BlockState state, final BlockState neighborState, final Direction direction) {
        return neighborState.is(Blocks.ICE) || (neighborState.hasProperty(Fluidloggable.FLUIDLOGGED) && neighborState.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) || super.skipRendering(state, neighborState, direction);
    }

    @Override
    public BlockState gbw$getOverlayState(BlockState state, BlockPos pos, RandomSource random) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            return Blocks.ICE.defaultBlockState();
        }
        return OverlayState.super.gbw$getOverlayState(state, pos, random);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Fluidloggable.melt(state, level, pos);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE)
            return true;
        return super.isRandomlyTicking(state);
    }

    @Override
    protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid())
            ticks.scheduleTick(pos, fluidType.getFluid(), fluidType.getFluid().getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }
}
