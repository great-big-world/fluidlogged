package dev.creoii.greatbigworld.fluidlogged.mixin.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.SpeleothemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(SpeleothemUtils.class)
public abstract class SpeleothemUtilsMixin {
    @Shadow
    protected static void buildBaseToTipColumn(Direction direction, int totalLength, boolean mergedTip, Consumer<BlockState> consumer, Block pointedBlock) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @WrapOperation(method = "growSpeleothem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/SpeleothemUtils;buildBaseToTipColumn(Lnet/minecraft/core/Direction;IZLjava/util/function/Consumer;Lnet/minecraft/world/level/block/Block;)V"))
    private static void gbw$generateFluidloggedPointedDripstone(Direction direction, int totalLength, boolean mergedTip, Consumer<BlockState> consumer, Block pointedBlock, Operation<Void> original, @Local(argsOnly = true, name = "level") LevelAccessor level, @Local(name = "pos") BlockPos.MutableBlockPos mutable) {
        buildBaseToTipColumn(direction, totalLength, mergedTip, state -> {
            if (state.getBlock() instanceof SpeleothemBlock) {
                if (level.getBlockState(mutable).is(Blocks.ICE)) {
                    state = state.setValue(Fluidloggable.FLUIDLOGGED, FluidType.ICE);
                } else state = state.setValue(Fluidloggable.FLUIDLOGGED, Fluidloggable.FLUIDS.get(level.getFluidState(mutable).getType()));
            }
            level.setBlock(mutable, state, Block.UPDATE_CLIENTS);
            mutable.move(direction);
        }, pointedBlock);
    }

    @Inject(method = "isEmptyOrWater(Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private static void gbw$allowGenerateInLavaOrIce(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.LAVA) || state.is(Blocks.ICE))
            cir.setReturnValue(true);
    }

    @Inject(method = "isNeitherEmptyNorWater", at = @At("HEAD"), cancellable = true)
    private static void gbw$donyDenyGenerateInLavaOrIce(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.LAVA) || state.is(Blocks.ICE))
            cir.setReturnValue(false);
    }
}
