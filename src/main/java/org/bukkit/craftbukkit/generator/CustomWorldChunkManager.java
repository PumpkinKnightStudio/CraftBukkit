package org.bukkit.craftbukkit.generator;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

public class CustomWorldChunkManager extends WorldChunkManager {

    private final WorldInfo worldInfo;
    private final BiomeProvider biomeProvider;
    private final IRegistry<BiomeBase> registry;

    private static List<BiomeBase> biomeListToBiomeBaseList(List<Biome> biomes, IRegistry<BiomeBase> registry) {
        List<BiomeBase> biomeBases = new ArrayList<>();

        for (Biome biome : biomes) {
            Preconditions.checkArgument(biome != Biome.CUSTOM, "Cannot use the biome %s", biome);
            biomeBases.add(CraftBiome.biomeToBiomeBase(registry, biome));
        }

        return biomeBases;
    }

    public CustomWorldChunkManager(WorldInfo worldInfo, BiomeProvider biomeProvider, IRegistry<BiomeBase> registry) {
        super(biomeListToBiomeBaseList(biomeProvider.getBiomes(worldInfo), registry));

        this.worldInfo = worldInfo;
        this.biomeProvider = biomeProvider;
        this.registry = registry;
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        throw new UnsupportedOperationException("Cannot serialize CustomWorldChunkManager");
    }

    @Override
    public WorldChunkManager a(long l) {
        // TODO check method further
        throw new UnsupportedOperationException("Cannot copy CustomWorldChunkManager");
    }

    @Override
    public BiomeBase getBiome(int x, int y, int z) {
        Biome biome = biomeProvider.getBiome(worldInfo, x << 2, y << 2, z << 2);
        Preconditions.checkArgument(biome != Biome.CUSTOM, "Cannot set the biome to %s", biome);

        return CraftBiome.biomeToBiomeBase(registry, biome);
    }
}
