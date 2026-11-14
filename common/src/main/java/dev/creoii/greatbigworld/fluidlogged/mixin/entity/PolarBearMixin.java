package dev.creoii.greatbigworld.fluidlogged.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PolarBear.class)
public class PolarBearMixin {
    @WrapOperation(method = "checkPolarBearSpawnRules", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private static boolean gbw$polarBearSpawnOnIceloggeds(BlockState instance, TagKey tagKey, Operation<Boolean> original) {
        return original.call(instance, tagKey) || (instance.hasProperty(Fluidloggable.FLUIDLOGGED) && instance.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE);
    }
}
