package dev.creoii.greatbigworld.fluidlogged.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Bee.class)
public class BeeMixin {
    @WrapOperation(method = "attractsBees", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValueOrElse(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Comparable;"))
    private static Comparable<?> gbw$fixAttractsBees(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Comparable<?>> original) {
        return instance.getValueOrElse(Fluidloggable.FLUIDLOGGED, FluidType.EMPTY) != FluidType.EMPTY;
    }
}
