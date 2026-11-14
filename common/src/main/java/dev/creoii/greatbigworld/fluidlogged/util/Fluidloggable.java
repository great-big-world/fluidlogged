package dev.creoii.greatbigworld.fluidlogged.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface Fluidloggable extends BucketPickup, LiquidBlockContainer {
    EnumProperty<FluidType> FLUIDLOGGED = EnumProperty.create("fluid", FluidType.class);
    Map<Fluid, FluidType> FLUIDS = new ImmutableMap.Builder<Fluid, FluidType>()
            .put(Fluids.EMPTY, FluidType.EMPTY)
            .put(Fluids.WATER, FluidType.WATER)
            .put(Fluids.FLOWING_WATER, FluidType.EMPTY)
            .put(Fluids.LAVA, FluidType.LAVA)
            .put(Fluids.FLOWING_LAVA, FluidType.EMPTY)
            .build();

    default boolean canPlaceLiquid(@Nullable LivingEntity filler, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return defaultCanPlaceLiquid(state);
    }

    default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return defaultPlaceLiquid(level, pos, state, fluidState);
    }

    default ItemStack pickupBlock(@Nullable LivingEntity drainer, LevelAccessor level, BlockPos pos, BlockState state) {
        return defaultPickupBlock(level, pos, state);
    }

    default Optional<SoundEvent> getPickupSound() {
        return defaultGetPickupSound();
    }

    static boolean canMelt(BlockState state, Level level, BlockPos pos) {
        return state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE && level.getBrightness(LightLayer.BLOCK, pos) > 11 - state.getLightDampening();
    }

    static void melt(BlockState state, Level level, BlockPos pos) {
        if (!canMelt(state, level, pos)) return;

        if (level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
            level.setBlockAndUpdate(pos, state.setValue(FLUIDLOGGED, FluidType.EMPTY));
        } else {
            BlockState melted = getMeltedState(state);
            level.setBlockAndUpdate(pos, melted);
            level.neighborChanged(pos, melted.getBlock(), null);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
        }
    }

    static BlockState getMeltedState(BlockState state) {
        if (state.getValue(FLUIDLOGGED) == FluidType.ICE)
            return state.setValue(FLUIDLOGGED, FluidType.WATER);
        return state;
    }

    static boolean defaultCanPlaceLiquid(BlockState state) {
        return state.getValue(FLUIDLOGGED) == FluidType.EMPTY;
    }

    static boolean defaultPlaceLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(FLUIDLOGGED) == FluidType.EMPTY) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, state.setValue(FLUIDLOGGED, FLUIDS.get(fluidState.getType())));
                level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            }
            return true;
        } else return false;
    }

    static ItemStack defaultPickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        FluidType fluidType = state.getValue(FLUIDLOGGED);
        if (fluidType.isLiquid()) {
            level.setBlockAndUpdate(pos, state.setValue(FLUIDLOGGED, FluidType.EMPTY));
            if (!state.canSurvive(level, pos)) {
                level.destroyBlock(pos, true);
            }

            return fluidType.getBucket().get();
        } else return ItemStack.EMPTY;
    }

    static Optional<SoundEvent> defaultGetPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    static Property<?>[] replaceWaterloggedWithFluidlogged(Property<?>[] properties) {
        for (int i = 0; i < properties.length; ++i) {
            if (properties[i] == BlockStateProperties.WATERLOGGED) {
                properties[i] = FLUIDLOGGED;
                break;
            }
        }
        return properties;
    }
}