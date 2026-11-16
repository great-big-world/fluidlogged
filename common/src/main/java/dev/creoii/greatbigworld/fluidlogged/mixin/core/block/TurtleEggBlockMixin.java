package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TurtleEggBlock.class)
public abstract class TurtleEggBlockMixin extends Block {
    public TurtleEggBlockMixin(Properties properties) {
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

    @Override
    protected FluidState getFluidState(BlockState state) {
        FluidType fluidType = state.getValue(Fluidloggable.FLUIDLOGGED);
        if (fluidType.isLiquid() && fluidType.getFluid() instanceof FlowingFluid flowableFluid) {
            return flowableFluid.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }
}
