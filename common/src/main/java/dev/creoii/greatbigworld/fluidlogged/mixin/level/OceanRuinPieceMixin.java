package dev.creoii.greatbigworld.fluidlogged.mixin.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OceanRuinPieces.OceanRuinPiece.class)
public class OceanRuinPieceMixin {
    @WrapOperation(method = "handleDataMarker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    private Object gbw$fixOceanRuinHandleDataMarker(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original, @Local(argsOnly = true, name = "position") BlockPos position, @Local(argsOnly = true, name = "level") ServerLevelAccessor level) {
        return original.call(instance, Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.getOrDefault(level.getFluidState(position).getType(), FluidType.EMPTY));
    }
}
