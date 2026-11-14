package dev.creoii.greatbigworld.fluidlogged.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelEventHandler.class)
public class LevelEventHandlerMixin {
    @Definition(id = "isWaterlogged", local = @Local(name = "isWaterlogged", type = boolean.class))
    @Expression("isWaterlogged == 0")
    @WrapOperation(method = "levelEvent", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private boolean injected(int left, int right, Operation<Boolean> original, @Local(name = "state") BlockState state) {
        boolean isWaterlikelogged = state.hasProperty(Fluidloggable.FLUIDLOGGED) && state.getValue(Fluidloggable.FLUIDLOGGED).isWaterlike();
        return original.call(left, right) || isWaterlikelogged;
    }
}
