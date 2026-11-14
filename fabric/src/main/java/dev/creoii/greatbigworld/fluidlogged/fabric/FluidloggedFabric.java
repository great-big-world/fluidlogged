package dev.creoii.greatbigworld.fluidlogged.fabric;

import dev.creoii.greatbigworld.fluidlogged.Fluidlogged;
import net.fabricmc.api.ModInitializer;

public final class FluidloggedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Fluidlogged.init();
    }
}
