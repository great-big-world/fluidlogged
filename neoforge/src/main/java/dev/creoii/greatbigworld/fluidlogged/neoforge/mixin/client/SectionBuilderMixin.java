package dev.creoii.greatbigworld.fluidlogged.neoforge.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.OverlayState;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
public abstract class SectionBuilderMixin {
    @Unique private static final float Z_FIGHT_PADDING = .0001f;
    @Shadow @Final private BlockStateModelSet blockModelSet;

    @WrapOperation(method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/client/renderer/block/BlockQuadOutput;FFFLnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;J)V"))
    private void gbw$renderBlockOverlays(ModelBlockRenderer instance, BlockQuadOutput output, float x, float y, float z, BlockAndTintGetter level, BlockPos pos, BlockState blockState, BlockStateModel model, long seed, Operation<Void> original) {
        if (blockState.getBlock() instanceof OverlayState overlayState && blockState.getRenderShape() == RenderShape.MODEL) {
            BlockState overlay = overlayState.gbw$getOverlayState(blockState, pos, null);
            if (overlay.getBlock() != Blocks.AIR) {
                original.call(instance, output, x + Z_FIGHT_PADDING, y + Z_FIGHT_PADDING, z + Z_FIGHT_PADDING, level, pos, overlay, blockModelSet.get(overlay), seed);
            }
        }

        original.call(instance, output, x, y, z, level, pos, blockState, model, seed);
    }
}
