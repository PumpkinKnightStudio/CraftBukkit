package org.bukkit.craftbukkit.generator;

import net.minecraft.world.level.biome.Climate;
import org.bukkit.generator.BiomeParameterPoint;

public class CraftBiomeParameterPoint implements BiomeParameterPoint {

    private final float temperature;
    private final float humidity;
    private final float continentalness;
    private final float erosion;
    private final float depth;
    private final float weirdness;

    public static BiomeParameterPoint createBiomeParameterPoint(Climate.h targetPoint) {
        return new CraftBiomeParameterPoint(targetPoint.temperature(), targetPoint.humidity(), targetPoint.continentalness(), targetPoint.erosion(), targetPoint.depth(), targetPoint.weirdness());
    }

    private CraftBiomeParameterPoint(float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.continentalness = continentalness;
        this.erosion = erosion;
        this.depth = depth;
        this.weirdness = weirdness;
    }

    @Override
    public float getTemperature() {
        return this.temperature;
    }

    @Override
    public float getHumidity() {
        return this.humidity;
    }

    @Override
    public float getContinentalness() {
        return this.continentalness;
    }

    @Override
    public float getErosion() {
        return this.erosion;
    }

    @Override
    public float getDepth() {
        return this.depth;
    }

    @Override
    public float getWeirdness() {
        return this.weirdness;
    }
}
