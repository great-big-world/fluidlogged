package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopperBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatheringCopperBarsBlock.class)
public class WeatheringCopperBarsBlockMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void gbw$ignoreWeatheringForFrozenCopperChains(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            Fluidloggable.melt(state, level, pos);
            ci.cancel();
        }
    }
}
