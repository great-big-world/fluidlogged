package dev.creoii.greatbigworld.fluidlogged.mixin;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import dev.creoii.greatbigworld.fluidlogged.Fluidlogged;
import dev.creoii.greatbigworld.fluidlogged.util.datafix.FluidloggingDataFix;
import dev.creoii.greatbigworld.fluidlogged.util.datafix.WaterloggingDataFix;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.filefix.FileFixerUpper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;

@Mixin(DataFixers.class)
public class DataFixersMixin {
    @Shadow @Final private static BiFunction<Integer, Schema, Schema> SAME_NAMESPACED;

    @Inject(method = "addFixers", at = @At("TAIL"))
    private static void gbw$addFluidloggedDataFixer(DataFixerBuilder fixerUpper, FileFixerUpper.Builder fileFixerUpper, CallbackInfo ci) {
        int version = SharedConstants.getCurrentVersion().dataVersion().version() - 1;
        Schema schema = fixerUpper.addSchema(version, SAME_NAMESPACED);
        switch (Fluidlogged.getFluidStateFixState()) {
            case CONVERT_TO_FLUIDLOGGED -> fixerUpper.addFixer(new FluidloggingDataFix(schema));
            case CONVERT_TO_WATERLOGGED -> fixerUpper.addFixer(new WaterloggingDataFix(schema));
        }
    }
}
