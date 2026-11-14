package dev.creoii.greatbigworld.fluidlogged.mixin.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @WrapOperation(method = "tickPrecipitation", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private boolean gbw$freezeWaterloggeds(ServerLevel instance, BlockPos blockPos, BlockState blockState, Operation<Boolean> original) {
        BlockState currentState = instance.getBlockState(blockPos);
        if (currentState.hasProperty(Fluidloggable.FLUIDLOGGED) && currentState.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.WATER) {
            return original.call(instance, blockPos, currentState.setValue(Fluidloggable.FLUIDLOGGED, FluidType.ICE));
        } else return original.call(instance, blockPos, blockState);
    }
}
