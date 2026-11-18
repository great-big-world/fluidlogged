package dev.creoii.greatbigworld.fluidlogged;

import dev.creoii.greatbigworld.fluidlogged.util.datafix.FluidStateFixState;

public final class Fluidlogged {
    public static final String NAMESPACE = "fluidlogged";
    public static final String GBW = "great_big_world";

    private static FluidStateFixState fluidStateFixState = FluidStateFixState.CONVERT_TO_FLUIDLOGGED;

    /**
     * TODO:
     * - Sniffer Egg
     * - Dried Ghast
     * - Corals
     * - Kelp & Seagrass
     * - Candles
     * - Candle Cakes
     * - Sculk Sensor
     * - Sculk Shrieker
     * - Dripleaf
     * - Hanging Roots
     * - Heavy Core
     */
    public static void init() {
    }

    public static FluidStateFixState getFluidStateFixState() {
        return fluidStateFixState;
    }

    public static void setFluidStateFixState(FluidStateFixState fluidStateFixState) {
        Fluidlogged.fluidStateFixState = fluidStateFixState;
    }
}
