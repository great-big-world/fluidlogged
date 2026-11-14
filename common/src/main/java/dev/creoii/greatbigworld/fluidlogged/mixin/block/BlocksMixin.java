package dev.creoii.greatbigworld.fluidlogged.mixin.block;

import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Blocks.class)
public class BlocksMixin {
    @Inject(method = "lambda$waterloggedMapColor$0", at = @At("HEAD"), cancellable = true)
    private static void gbw$fixWaterloggedMapColor(MapColor mapColor, BlockState blockState, CallbackInfoReturnable<MapColor> cir) {
        if (blockState.hasProperty(Fluidloggable.FLUIDLOGGED)) {
            cir.setReturnValue(switch (blockState.getValue(Fluidloggable.FLUIDLOGGED)) {
                case EMPTY -> mapColor;
                case WATER -> MapColor.WATER;
                case LAVA -> MapColor.FIRE;
                case ICE -> MapColor.ICE;
            });
        }
    }
}
