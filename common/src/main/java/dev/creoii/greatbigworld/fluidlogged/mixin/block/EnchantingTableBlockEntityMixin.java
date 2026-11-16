package dev.creoii.greatbigworld.fluidlogged.mixin.block;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableBlockEntity.class)
public class EnchantingTableBlockEntityMixin {
    @Inject(method = "bookAnimationTick", at = @At("HEAD"), cancellable = true)
    private static void gbw$disableBookAnimationWhenIcelogged(Level level, BlockPos worldPosition, BlockState state, EnchantingTableBlockEntity entity, CallbackInfo ci) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            ci.cancel();
        }
    }
}
