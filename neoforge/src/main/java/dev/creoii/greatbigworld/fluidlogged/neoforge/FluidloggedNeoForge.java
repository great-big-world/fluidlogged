package dev.creoii.greatbigworld.fluidlogged.neoforge;

import dev.creoii.greatbigworld.fluidlogged.Fluidlogged;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("fluidlogged")
public final class FluidloggedNeoForge {
    public FluidloggedNeoForge(IEventBus modBus) {
        Fluidlogged.init();
    }
}
