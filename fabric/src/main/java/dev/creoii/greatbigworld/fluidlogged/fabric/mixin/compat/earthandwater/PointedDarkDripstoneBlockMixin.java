package dev.creoii.greatbigworld.fluidlogged.fabric.mixin.compat.earthandwater;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import dev.creoii.greatbigworld.fluidlogged.util.OverlayState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import potatowolfie.earth_and_water.block.custom.PointedDarkDripstoneBlock;

import java.util.Optional;

@Mixin(PointedDarkDripstoneBlock.class)
public abstract class PointedDarkDripstoneBlockMixin extends SpeleothemBlock implements Fluidloggable, OverlayState {
    public PointedDarkDripstoneBlockMixin(BlockState blockToGrowOn, Properties properties) {
        super(blockToGrowOn, properties);
    }

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

    @WrapOperation(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 2))
    private Object gbw$fixFluidloggablePlacementState(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original, @Local(name = "worldAccess") LevelAccessor worldAccess, @Local(name = "blockPos") BlockPos blockPos) {
        return original.call(instance, Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.get(worldAccess.getFluidState(blockPos).getType()));
    }

    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 2))
    private static Object gbw$fixPlace(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original, @Local(argsOnly = true, name = "world") LevelAccessor world, @Local(argsOnly = true, name = "pos") BlockPos pos) {
        FluidType fluidType = FluidType.EMPTY;
        Fluid fluid = world.getFluidState(pos).getType();
        if (world.getBlockState(pos).is(Blocks.ICE)) {
            fluidType = FluidType.ICE;
        } else if (Fluidloggable.FLUIDS.containsKey(fluid)) {
            fluidType = Fluidloggable.FLUIDS.get(fluid);
        }
        return original.call(instance, Fluidloggable.FLUIDLOGGED, fluidType);
    }

    @Inject(method = "isCollisionShapeFullBlock", at = @At("HEAD"), cancellable = true)
    private void gbw$fixFullCollisionForIce(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPathfindable", at = @At("HEAD"), cancellable = true)
    private void gbw$fixPathfindableForIce(BlockState state, PathComputationType type, CallbackInfoReturnable<Boolean> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void gbw$handleIceMelt(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        Fluidloggable.melt(state, level, pos);
    }

    @Inject(method = "onProjectileHit", at = @At("HEAD"), cancellable = true)
    private void gbw$fixProjectileHit(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile, CallbackInfo ci) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            ci.cancel();
        }
    }

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void gbw$fixFallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            super.fallOn(level, state, pos, entity, fallDistance);
            ci.cancel();
        }
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void gbw$injectFullShapeForIce(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(Shapes.block());
        }
    }

    @Inject(method = "getOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void gbw$injectFullOcclusionShapeForIce(BlockState state, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            cir.setReturnValue(Shapes.block());
        }
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
}
