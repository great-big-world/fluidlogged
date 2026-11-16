package dev.creoii.greatbigworld.fluidlogged.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public final class FluidloggedPlatformUtil {
    @ExpectPlatform
    public static boolean isModLoaded(String namespace) {
        throw new AssertionError();
    }
}
