package dev.creoii.greatbigworld.fluidlogged.mixin.level;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RootPlacer.class)
public class RootPlacerMixin {
    @Inject(method = "getPotentiallyWaterloggedState", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void gbw$getPotentiallyFluidloggedState(LevelSimulatedReader level, BlockPos pos, BlockState state, CallbackInfoReturnable<BlockState> cir) {
        if (state.hasProperty(Fluidloggable.FLUIDLOGGED)) {
            boolean waterlogged = level.isFluidAtPosition(pos, (s) -> s.is(FluidTags.WATER));
            cir.setReturnValue(state.setValue(Fluidloggable.FLUIDLOGGED, waterlogged ? FluidType.WATER : FluidType.EMPTY));
        }
    }
}
