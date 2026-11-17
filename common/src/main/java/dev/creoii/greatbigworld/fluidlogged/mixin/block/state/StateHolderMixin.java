package dev.creoii.greatbigworld.fluidlogged.mixin.block.state;

import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StateHolder.class)
public abstract class StateHolderMixin {
    @Shadow protected abstract int valueIndex(Property<?> property);
    @Shadow public abstract boolean hasProperty(Property<?> property);
    @Shadow public abstract <T extends Comparable<T>, V extends T> Object setValue(Property<T> property, V value);
    @Shadow protected abstract <T extends Comparable<T>> T getNullableValue(Property<T> property);

    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
    private <T extends Comparable<T>> void gbw$fixGetWaterlogged(Property<T> property, CallbackInfoReturnable<Boolean> cir) {
        T value = getNullableValue(property);
        if (value == null && property == BlockStateProperties.WATERLOGGED) {
            FluidType fluidType = getNullableValue(Fluidloggable.FLUIDLOGGED);
            if (fluidType == FluidType.WATER)
                cir.setReturnValue(true);
        }
    }

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    private <T extends Comparable<T>, V extends T> void gbw$fixSetWaterlogged(Property<T> property, V value, CallbackInfoReturnable<Object> cir) {
        if (property == BlockStateProperties.WATERLOGGED) {
            int index = valueIndex(property);
            if (index == -1 && hasProperty(Fluidloggable.FLUIDLOGGED)) {
                cir.setReturnValue(setValue(Fluidloggable.FLUIDLOGGED, FluidType.WATER));
            }
        }
    }
}
