package dev.creoii.greatbigworld.fluidlogged.mixin.level;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WaterloggedVegetationPatchFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterloggedVegetationPatchFeature.class)
public class WaterloggedVegetationPatchFeatureMixin {
    @Inject(method = "placeVegetation", at = @At(value = "RETURN", ordinal = 0))
    private void gbw$fixPlaceVegetation(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos placementPos, CallbackInfoReturnable<Boolean> cir, @Local(name = "placed") BlockState placed) {
        if (!placed.hasProperty(BlockStateProperties.WATERLOGGED) && placed.hasProperty(Fluidloggable.FLUIDLOGGED) && placed.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.EMPTY) {
            level.setBlock(placementPos, placed.setValue(Fluidloggable.FLUIDLOGGED, FluidType.WATER), 2);
        }
    }
}
