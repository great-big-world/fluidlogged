package dev.creoii.greatbigworld.fluidlogged.mixin.block;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {
    @Inject(method = "particleTick", at = @At("HEAD"), cancellable = true)
    private static void gbw$disableParticleTickWhenIcelogged(Level level, BlockPos pos, BlockState state, CampfireBlockEntity entity, CallbackInfo ci) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            ci.cancel();
        }
    }
}
