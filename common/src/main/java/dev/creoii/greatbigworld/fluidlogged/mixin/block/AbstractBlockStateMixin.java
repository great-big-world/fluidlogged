package dev.creoii.greatbigworld.fluidlogged.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.ToIntFunction;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin {
    @Shadow protected abstract BlockState asState();

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/function/ToIntFunction;applyAsInt(Ljava/lang/Object;)I"))
    private <T> int gbw$fixLightForLavalogged(ToIntFunction<BlockState> instance, T t, Operation<Integer> original) {
        BlockState state = asState();
        if (state.hasProperty(Fluidloggable.FLUIDLOGGED) && state.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.LAVA)
            return 15;
        return instance.applyAsInt(state);
    }
}
