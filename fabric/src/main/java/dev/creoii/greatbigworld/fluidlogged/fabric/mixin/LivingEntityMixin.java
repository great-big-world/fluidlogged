package dev.creoii.greatbigworld.fluidlogged.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @WrapOperation(method = "travelInAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"))
    private float gbw$applyIceloggedFriction(Block instance, Operation<Float> original, @Local(name = "posBelow") BlockPos posBelow) {
        BlockState state = level().getBlockState(posBelow);
        if (state.hasProperty(Fluidloggable.FLUIDLOGGED) && state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            return Blocks.ICE.getFriction();
        }
        return original.call(instance);
    }
}
