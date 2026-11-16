package dev.creoii.greatbigworld.fluidlogged.platform.neoforge;

import net.neoforged.fml.loading.FMLLoader;

public final class FluidloggedPlatformUtilImpl {
    public static boolean isModLoaded(String namespace) {
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(namespace) != null;
    }
}
