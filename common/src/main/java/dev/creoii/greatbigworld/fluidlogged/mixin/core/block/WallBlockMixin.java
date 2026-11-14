package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin extends Block {
    public WallBlockMixin(Properties settings) {
        super(settings);
    }

    @WrapOperation(method = "makeShapes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WallBlock;getShapeForEachState(Ljava/util/function/Function;[Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/util/function/Function;"))
    private Function<BlockState, VoxelShape> gbw$removeWaterloggedFromMakeShapes(WallBlock instance, Function<BlockState, VoxelShape> function, Property<?>[] properties1, Operation<Function<BlockState, VoxelShape>> original) {
        return original.call(instance, function, new Property<?>[]{});
    }

    @Inject(method = "getShape", at = @At(value = "HEAD"), cancellable = true)
    private static void gbw$fixShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE)
            cir.setReturnValue(Shapes.block());
    }

    @Inject(method = "getCollisionShape", at = @At(value = "HEAD"), cancellable = true)
    private static void gbw$fixCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            VoxelShape shape = cir.getReturnValue();
            cir.setReturnValue(shape == null ? Shapes.block() : Shapes.or(Shapes.block(), shape));
        }
    }

    @WrapOperation(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    private Object gbw$fixFluidloggablePlacementState(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original, @Local(name = "replacedFluidState") FluidState replacedFluidState) {
        return original.call(instance, Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.get(replacedFluidState.getType()));
    }

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

    @WrapOperation(method = "getFluidState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0))
    private Comparable<?> gbw$fixFluidloggableFluidState(BlockState instance, Property<?> property, Operation<Comparable<?>> original) {
        return instance.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.WATER;
    }

    @WrapOperation(method = "getFluidState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFluidState(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/material/FluidState;"))
    private FluidState gbw$fixFluidloggableFluidState(WallBlock instance, BlockState state, Operation<FluidState> original) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid() && fluidType.getFluid() instanceof FlowingFluid flowableFluid) {
            return flowableFluid.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Inject(method = "isPathfindable", at = @At("HEAD"), cancellable = true)
    private void gbw$fixPathfindableForIce(BlockState state, PathComputationType type, CallbackInfoReturnable<Boolean> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
    private void gbw$fixPropagatesSkylightDown(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.EMPTY) {
            cir.setReturnValue(true);
        } else cir.setReturnValue(false);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Fluidloggable.melt(state, level, pos);
    }
}
