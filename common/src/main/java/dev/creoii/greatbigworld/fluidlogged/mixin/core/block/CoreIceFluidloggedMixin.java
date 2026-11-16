package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import dev.creoii.greatbigworld.fluidlogged.util.OverlayState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
        SpeleothemBlock.class,
        SlabBlock.class,
        StairBlock.class,
        WallBlock.class,
        AmethystClusterBlock.class,
        ChainBlock.class,
        RodBlock.class,
        CrossCollisionBlock.class,
        FenceGateBlock.class,
        BrewingStandBlock.class,

        AnvilBlock.class,
        BedBlock.class,
        StrawBedBlock.class,
        ShulkerBoxBlock.class,
        BellBlock.class,
        CakeBlock.class,
        ComposterBlock.class,
        DaylightDetectorBlock.class,
        TrapDoorBlock.class,
        DoorBlock.class,
        EndPortalFrameBlock.class,
        GrindstoneBlock.class,
        HopperBlock.class,
        LecternBlock.class,
        PistonBaseBlock.class,
        PistonHeadBlock.class,
        StonecutterBlock.class,
        TurtleEggBlock.class,

        LadderBlock.class,
        ShelfBlock.class,
        CampfireBlock.class,
        DecoratedPotBlock.class,
        ScaffoldingBlock.class,
        SignBlock.class,
        MultifaceBlock.class,
        LanternBlock.class,
        ChestBlock.class,
        EnderChestBlock.class,
        CopperGolemStatueBlock.class,
        DragonEggBlock.class,
        EnchantingTableBlock.class
})
public abstract class CoreIceFluidloggedMixin extends Block implements OverlayState {
    public CoreIceFluidloggedMixin(Properties properties) {
        super(properties);
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
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Fluidloggable.melt(state, level, pos);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE)
            return true;
        return super.isRandomlyTicking(state);
    }
}
