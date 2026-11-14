package dev.creoii.greatbigworld.fluidlogged.mixin.level;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class BiomeMixin {
    @Inject(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Ljava/lang/Object;)Z"), cancellable = true)
    private void gbw$freezeWaterloggables(LevelReader level, BlockPos pos, boolean checkNeighbors, CallbackInfoReturnable<Boolean> cir, @Local(name = "blockState") BlockState blockState, @Local(name = "fluidState") FluidState fluidState) {
        if (blockState.hasProperty(Fluidloggable.FLUIDLOGGED) && blockState.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.WATER) {
            if (!checkNeighbors) {
                cir.setReturnValue(true);
            }

            boolean surroundedByWater = level.isWaterAt(pos.west()) && level.isWaterAt(pos.east()) && level.isWaterAt(pos.north()) && level.isWaterAt(pos.south());
            if (!surroundedByWater) {
                cir.setReturnValue(true);
            }
        }
    }
}
