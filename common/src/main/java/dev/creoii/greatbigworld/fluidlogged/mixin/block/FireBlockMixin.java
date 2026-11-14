package dev.creoii.greatbigworld.fluidlogged.mixin.block;

import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @Inject(method = "getBurnOdds", at = @At("HEAD"), cancellable = true)
    private void gbw$fixBurnOddsForFluidloggeds(BlockState state, CallbackInfoReturnable<Integer> cir) {
        if (state.hasProperty(Fluidloggable.FLUIDLOGGED) && state.getValue(Fluidloggable.FLUIDLOGGED).isWaterlike())
            cir.setReturnValue(0);
    }
    @Inject(method = "getIgniteOdds(Lnet/minecraft/world/level/block/state/BlockState;)I", at = @At("HEAD"), cancellable = true)
    private void gbw$fixIgniteOddsForFluidloggeds(BlockState state, CallbackInfoReturnable<Integer> cir) {
        if (state.hasProperty(Fluidloggable.FLUIDLOGGED) && state.getValue(Fluidloggable.FLUIDLOGGED).isWaterlike())
            cir.setReturnValue(0);
    }
}
