package dev.creoii.greatbigworld.fluidlogged.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuddingAmethystBlock.class)
public class BuddingAmethystBlockMixin {
    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 1))
    private Object gbw$fixAmethystGrow(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original, @Local(name = "relativeState") BlockState relativeState) {
        FluidState fluidState = relativeState.getFluidState();
        return original.call(instance, Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.get(fluidState.getType()));
    }

    @Inject(method = "canClusterGrowAtState", at = @At("HEAD"), cancellable = true)
    private static void gbw$fixCanClusterGrowAtState(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.LAVA))
            cir.setReturnValue(true);
    }
}
