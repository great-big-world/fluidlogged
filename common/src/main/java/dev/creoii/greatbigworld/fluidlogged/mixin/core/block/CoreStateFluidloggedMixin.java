package dev.creoii.greatbigworld.fluidlogged.mixin.core.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.greatbigworld.fluidlogged.util.FluidType;
import dev.creoii.greatbigworld.fluidlogged.util.Fluidloggable;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({
        SpeleothemBlock.class,
        SlabBlock.class,
        StairBlock.class,
        WallBlock.class,
        AmethystClusterBlock.class,
        ChainBlock.class,
        LightningRodBlock.class,
        FenceBlock.class,
        IronBarsBlock.class
})
public class CoreStateFluidloggedMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    private Object gbw$setFluidloggableDefaultState(BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original) {
        if (property == BlockStateProperties.WATERLOGGED)
            return original.call(instance, Fluidloggable.FLUIDLOGGED, FluidType.EMPTY);
        return original.call(instance, property, comparable);
    }

    @WrapOperation(method = "createBlockStateDefinition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"))
    private StateDefinition.Builder<Block, BlockState> gbw$addFluidloggableProperty(StateDefinition.Builder<Block, BlockState> instance, Property<?>[] properties, Operation<StateDefinition.Builder<Block, BlockState>> original) {
        return original.call(instance, Fluidloggable.replaceWaterloggedWithFluidlogged(properties));
    }
}
