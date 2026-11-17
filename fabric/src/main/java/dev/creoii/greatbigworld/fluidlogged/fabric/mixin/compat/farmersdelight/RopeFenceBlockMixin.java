package dev.creoii.greatbigworld.fluidlogged.mixin.compat.farmersdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.RopeFenceBlock;

@Mixin(RopeFenceBlock.class)
public abstract class RopeFenceBlockMixin extends CrossCollisionBlock {
    protected RopeFenceBlockMixin(float postWidth, float postHeight, float wallWidth, float wallHeight, float collisionHeight, Properties properties) {
        super(postWidth, postHeight, wallWidth, wallHeight, collisionHeight, properties);
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

    @WrapOperation(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 4))
    private Object gbw$fixFluidloggablePlacementState(BlockState instance, Property property, Comparable comparable, Operation<Object> original, @Local(argsOnly = true, name = "context") BlockPlaceContext context) {
        return original.call(instance, Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.get(context.getLevel().getFluidState(context.getClickedPos()).getType()));
    }

    @Inject(method = "getOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void gbw$injectFullOcclusionShapeForIce(BlockState state, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(Shapes.block());
        }
    }

    @Inject(method = "getVisualShape", at = @At("HEAD"), cancellable = true)
    private void gbw$injectFullVisualShapeForIce(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(Shapes.block());
        }
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

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void gbw$disableUseItemWhenIcelogged(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
