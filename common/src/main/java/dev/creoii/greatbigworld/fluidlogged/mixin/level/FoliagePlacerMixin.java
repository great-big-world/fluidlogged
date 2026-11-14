package dev.creoii.greatbigworld.fluidlogged.mixin.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoliagePlacer.class)
public class FoliagePlacerMixin {
    @WrapOperation(method = "tryPlaceLeaf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/foliageplacers/FoliagePlacer$FoliageSetter;set(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private static void gbw$fixTryPlaceLeaf(FoliagePlacer.FoliageSetter instance, BlockPos blockPos, BlockState state, Operation<Void> original) {
        if (state.hasProperty(Fluidloggable.FLUIDLOGGED) && state.getValue(Fluidloggable.FLUIDLOGGED) != FluidType.EMPTY) {
            state = state.setValue(Fluidloggable.FLUIDLOGGED, FluidType.EMPTY);
        }
        original.call(instance, blockPos, state);
    }
}
