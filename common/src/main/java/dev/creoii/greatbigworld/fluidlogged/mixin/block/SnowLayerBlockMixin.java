package dev.creoii.greatbigworld.fluidlogged.mixin.block;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowLayerBlock.class)
public class SnowLayerBlockMixin {
    @Inject(method = "canSurvive", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private void gbw$fixSnowLayersOnIceloggeds(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState belowState = level.getBlockState(pos.below());
        if (belowState.hasProperty(Fluidloggable.FLUIDLOGGED) && belowState.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE)
            cir.setReturnValue(false);
    }
}
