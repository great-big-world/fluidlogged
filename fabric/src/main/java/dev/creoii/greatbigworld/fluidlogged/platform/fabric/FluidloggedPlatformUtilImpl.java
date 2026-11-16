package dev.creoii.greatbigworld.fluidlogged.platform.fabric;

import net.fabricmc.loader.api.FabricLoader;

public final class FluidloggedPlatformUtilImpl {
    public static boolean isModLoaded(String namespace) {
        return FabricLoader.getInstance().isModLoaded(namespace);
    }
}
