package dev.creoii.greatbigworld.fluidlogged.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {
    @WrapOperation(method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"))
    private float gbw$applyIceDestroySpeedForIceloggeds(ItemStack instance, BlockState state, Operation<Float> original) {
        if (state.hasProperty(Fluidloggable.FLUIDLOGGED) && state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE)
            return original.call(instance, Blocks.ICE.defaultBlockState());
        return original.call(instance, state);
    }
}
