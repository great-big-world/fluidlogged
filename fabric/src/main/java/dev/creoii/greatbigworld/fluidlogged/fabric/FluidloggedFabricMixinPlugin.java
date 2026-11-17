package dev.creoii.greatbigworld.fluidlogged.fabric;

import dev.creoii.greatbigworld.fluidlogged.platform.FluidloggedPlatformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FluidloggedFabricMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if ("dev.creoii.greatbigworld.fluidlogged.fabric.mixin.compat.farmersdelight.RopeBlockMixin".equals(mixinClassName)) {
            return FluidloggedPlatformUtil.isModLoaded("farmersdelight");
        }
        if ("dev.creoii.greatbigworld.fluidlogged.fabric.mixin.compat.farmersdelight.RopeFenceBlockMixin".equals(mixinClassName)) {
            return FluidloggedPlatformUtil.isModLoaded("farmersdelight");
        }
        if ("dev.creoii.greatbigworld.fluidlogged.fabric.mixin.compat.earthandwater.PointedDarkDripstoneBlockMixin".equals(mixinClassName)) {
            return FluidloggedPlatformUtil.isModLoaded("earth-and-water");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}