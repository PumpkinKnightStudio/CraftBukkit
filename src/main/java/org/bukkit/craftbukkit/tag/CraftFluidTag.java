package org.bukkit.craftbukkit.tag;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.FluidType;
import org.bukkit.Bukkit;
import org.bukkit.Fluid;
import org.bukkit.craftbukkit.CraftFluid;
import org.bukkit.craftbukkit.CraftServer;

public class CraftFluidTag extends CraftTag<FluidType, Fluid> {

    public CraftFluidTag(IRegistry<FluidType> registry, TagKey<FluidType> tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(Fluid fluid) {
        return CraftFluid.bukkitToMinecraft(((CraftServer) Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registries.FLUID), fluid).is(tag);
    }

    @Override
    public Set<Fluid> getValues() {
        return getHandle().stream().map((fluid) -> CraftFluid.minecraftToBukkit(((CraftServer) Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registries.FLUID), fluid.value())).collect(Collectors.toUnmodifiableSet());
    }
}
