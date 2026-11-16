package dev.creoii.greatbigworld.fluidlogged.mixin.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @Shadow @Final protected Fluid content;
    @Shadow protected abstract void playEmptySound(@Nullable LivingEntity user, LevelAccessor world, BlockPos pos);

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BucketItem;emptyContents(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;)Z"))
    private BlockPos gbw$fixFluidloggingFilling(BlockPos pos, @Local(argsOnly = true, name = "level") Level world, @Local(argsOnly = true, name = "player") Player user, @Local(name = "clicked") BlockState clicked, @Local(name = "pos") BlockPos blockPos, @Local(name = "directionOffsetPos") BlockPos directionOffsetPos) {
        return clicked.getBlock() instanceof LiquidBlockContainer fluidFillable && fluidFillable.canPlaceLiquid(user, world, pos, clicked, content) ? blockPos : directionOffsetPos;
    }

    @Inject(method = "emptyContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"), cancellable = true)
    private void gbw$fixFluidloggingFilling(LivingEntity user, Level level, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir, @Local(name = "flowingFluid") FlowingFluid flowableFluid, @Local(name = "block") Block block, @Local(name = "blockState") BlockState blockState) {
        if (block instanceof LiquidBlockContainer fluidFillable) {
            fluidFillable.placeLiquid(level, pos, blockState, flowableFluid.getSource(false));
            playEmptySound(user, level, pos);
            cir.setReturnValue(true);
        }
    }
}
