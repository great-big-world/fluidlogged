package dev.creoii.greatbigworld.fluidlogged.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow protected ServerLevel level;
    @Shadow @Final protected ServerPlayer player;
    @Shadow private GameType gameModeForPlayer;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean gbw$handleIceloggedWaterConversion(ServerPlayerGameMode instance, BlockPos pos, Operation<Boolean> original, @Local(name = "blockState") BlockState blockState) {
        if (blockState.hasProperty(Fluidloggable.FLUIDLOGGED) && blockState.getValue(Fluidloggable.FLUIDLOGGED) == FluidType.ICE) {
            return replaceWithWaterlogged(blockState, pos);
        }
        return original.call(instance, pos);
    }
    
    @Unique
    public boolean replaceWithWaterlogged(BlockState state, BlockPos pos) {
        if (!player.getMainHandItem().canDestroyBlock(state, level, pos, player)) {
            return false;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            Block block = state.getBlock();
            if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
                level.sendBlockUpdated(pos, state, state, 3);
                return false;
            } else if (player.blockActionRestricted(level, pos, gameModeForPlayer)) {
                return false;
            } else {
                BlockState replaced = state.setValue(Fluidloggable.FLUIDLOGGED, FluidType.WATER);
                level.setBlockAndUpdate(pos, replaced);

                if (!player.preventsBlockDrops()) {
                    ItemStack itemStack = player.getMainHandItem();
                    ItemStack destroyedWith = itemStack.copy();
                    boolean canDestroy = player.hasCorrectToolForDrops(replaced);
                    itemStack.mineBlock(level, replaced, pos, player);
                    if (canDestroy) {
                        block.playerDestroy(level, player, pos, replaced, blockEntity, destroyedWith);
                    }

                }
                return true;
            }
        }
    }
}
