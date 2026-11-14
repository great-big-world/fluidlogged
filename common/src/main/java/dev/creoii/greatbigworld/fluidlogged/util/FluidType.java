package dev.creoii.greatbigworld.fluidlogged.util;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;

public enum FluidType implements StringRepresentable {
    EMPTY("empty", Fluids.EMPTY, () -> ItemStack.EMPTY),
    WATER("water", Fluids.WATER, () -> Items.WATER_BUCKET.getDefaultInstance()), // DO NOT USE METHOD REFERENCES HERE, IT BREAKS EVERYTHING FOR SOME REASON! SHUT UP INTELLIJ!
    LAVA("lava", Fluids.LAVA, () -> Items.LAVA_BUCKET.getDefaultInstance()),
    ICE("ice", Fluids.EMPTY, () -> ItemStack.EMPTY);

    private final String name;
    private final Fluid fluid;
    private final Supplier<ItemStack> bucket;

    FluidType(String name, Fluid fluid, Supplier<ItemStack> bucket) {
        this.name = name;
        this.fluid = fluid;
        this.bucket = bucket;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public boolean isLiquid() {
        return getFluid() != Fluids.EMPTY;
    }

    public boolean isWaterlike() {
        return this == WATER || this == ICE;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public Supplier<ItemStack> getBucket() {
        return bucket;
    }
}
