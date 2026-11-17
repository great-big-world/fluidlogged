package dev.creoii.greatbigworld.fluidlogged.mixin.block;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin extends SpeleothemBlock {
    public PointedDripstoneBlockMixin(BlockState blockToGrowOn, Properties properties) {
        super(blockToGrowOn, properties);
    }

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void gbw$fixFallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        if (state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            super.fallOn(level, state, pos, entity, fallDistance);
            ci.cancel();
        }
    }
}
