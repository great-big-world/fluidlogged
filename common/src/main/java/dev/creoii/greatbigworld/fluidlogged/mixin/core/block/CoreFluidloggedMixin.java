package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import dev.creoii.greatbigworld.fluidlogged.util.OverlayState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin({
        SpeleothemBlock.class,
        SlabBlock.class,
        StairBlock.class,
        WallBlock.class,
        AmethystClusterBlock.class,
        ChainBlock.class
})
@Implements(@Interface(iface = Fluidloggable.class, prefix = "fluidlogged$"))
public abstract class CoreFluidloggedMixin extends Block implements OverlayState {
    public CoreFluidloggedMixin(Properties properties) {
        super(properties);
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

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void gbw$injectFullShapeForIce(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
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

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE)
            return true;
        return super.isRandomlyTicking(state);
    }
}
