package net.minecraft.world.level.levelgen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.SeededRandom.a;
import net.minecraft.world.level.levelgen.SurfaceRules.o;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;

public final class GeneratorSettingBase {
    public static final Codec<GeneratorSettingBase> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
        return var0.group(StructureSettings.CODEC.fieldOf("structures").forGetter(GeneratorSettingBase::structureSettings), NoiseSettings.CODEC.fieldOf("noise").forGetter(GeneratorSettingBase::noiseSettings), IBlockData.CODEC.fieldOf("default_block").forGetter(GeneratorSettingBase::getDefaultBlock), IBlockData.CODEC.fieldOf("default_fluid").forGetter(GeneratorSettingBase::getDefaultFluid), o.CODEC.fieldOf("surface_rule").forGetter(GeneratorSettingBase::surfaceRule), Codec.INT.fieldOf("sea_level").forGetter(GeneratorSettingBase::seaLevel), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(GeneratorSettingBase::disableMobGeneration), Codec.BOOL.fieldOf("aquifers_enabled").forGetter(GeneratorSettingBase::isAquifersEnabled), Codec.BOOL.fieldOf("noise_caves_enabled").forGetter(GeneratorSettingBase::isNoiseCavesEnabled), Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(GeneratorSettingBase::isOreVeinsEnabled), Codec.BOOL.fieldOf("noodle_caves_enabled").forGetter(GeneratorSettingBase::isNoodleCavesEnabled), Codec.BOOL.fieldOf("legacy_random_source").forGetter(GeneratorSettingBase::useLegacyRandomSource)).apply(var0, GeneratorSettingBase::new);
    });
    public static final Codec<Supplier<GeneratorSettingBase>> CODEC;
    private final a randomSource;
    private final StructureSettings structureSettings;
    private final NoiseSettings noiseSettings;
    private final IBlockData defaultBlock;
    private final IBlockData defaultFluid;
    private final o surfaceRule;
    private final int seaLevel;
    private final boolean disableMobGeneration;
    private final boolean aquifersEnabled;
    private final boolean noiseCavesEnabled;
    private final boolean oreVeinsEnabled;
    private final boolean noodleCavesEnabled;
    public static final ResourceKey<GeneratorSettingBase> OVERWORLD;
    public static final ResourceKey<GeneratorSettingBase> LARGE_BIOMES;
    public static final ResourceKey<GeneratorSettingBase> AMPLIFIED;
    public static final ResourceKey<GeneratorSettingBase> NETHER;
    public static final ResourceKey<GeneratorSettingBase> END;
    public static final ResourceKey<GeneratorSettingBase> CAVES;
    public static final ResourceKey<GeneratorSettingBase> FLOATING_ISLANDS;

    public GeneratorSettingBase(StructureSettings var0, NoiseSettings var1, IBlockData var2, IBlockData var3, o var4, int var5, boolean var6, boolean var7, boolean var8, boolean var9, boolean var10, boolean var11) {
        this.structureSettings = var0;
        this.noiseSettings = var1;
        this.defaultBlock = var2;
        this.defaultFluid = var3;
        this.surfaceRule = var4;
        this.seaLevel = var5;
        this.disableMobGeneration = var6;
        this.aquifersEnabled = var7;
        this.noiseCavesEnabled = var8;
        this.oreVeinsEnabled = var9;
        this.noodleCavesEnabled = var10;
        this.randomSource = var11 ? a.LEGACY : a.XOROSHIRO;
    }

    public StructureSettings structureSettings() {
        return this.structureSettings;
    }

    public NoiseSettings noiseSettings() {
        return this.noiseSettings;
    }

    public IBlockData getDefaultBlock() {
        return this.defaultBlock;
    }

    public IBlockData getDefaultFluid() {
        return this.defaultFluid;
    }

    public o surfaceRule() {
        return this.surfaceRule;
    }

    public int seaLevel() {
        return this.seaLevel;
    }

    /** @deprecated */
    @Deprecated
    protected boolean disableMobGeneration() {
        return this.disableMobGeneration;
    }

    public boolean isAquifersEnabled() {
        return this.aquifersEnabled;
    }

    public boolean isNoiseCavesEnabled() {
        return this.noiseCavesEnabled;
    }

    public boolean isOreVeinsEnabled() {
        return this.oreVeinsEnabled;
    }

    public boolean isNoodleCavesEnabled() {
        return this.noodleCavesEnabled;
    }

    public boolean useLegacyRandomSource() {
        return this.randomSource == a.LEGACY;
    }

    public RandomSource createRandomSource(long var0) {
        return this.getRandomSource().newInstance(var0);
    }

    public a getRandomSource() {
        return this.randomSource;
    }

    public boolean stable(ResourceKey<GeneratorSettingBase> var0) {
        return Objects.equals(this, RegistryGeneration.NOISE_GENERATOR_SETTINGS.get(var0));
    }

    private static void register(ResourceKey<GeneratorSettingBase> var0, GeneratorSettingBase var1) {
        RegistryGeneration.register(RegistryGeneration.NOISE_GENERATOR_SETTINGS, var0.location(), var1);
    }

    public static GeneratorSettingBase bootstrap() {
        return (GeneratorSettingBase)RegistryGeneration.NOISE_GENERATOR_SETTINGS.iterator().next();
    }

    private static GeneratorSettingBase end() {
        return new GeneratorSettingBase(new StructureSettings(false), NoiseSettings.create(0, 128, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-23.4375D, 64, -46), new NoiseSlider(-0.234375D, 7, 1), 2, 1, true, false, false, TerrainProvider.end()), Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), SurfaceRuleData.end(), 0, true, false, false, false, false, true);
    }

    private static GeneratorSettingBase nether() {
        Map<StructureGenerator<?>, StructureSettingsFeature> var0 = Maps.newHashMap(StructureSettings.DEFAULTS);
        var0.put(StructureGenerator.RUINED_PORTAL, new StructureSettingsFeature(25, 10, 34222645));
        return new GeneratorSettingBase(new StructureSettings(Optional.empty(), var0), NoiseSettings.create(0, 128, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlider(0.9375D, 3, 0), new NoiseSlider(2.5D, 4, -1), 1, 2, false, false, false, TerrainProvider.nether()), Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), SurfaceRuleData.nether(), 32, false, false, false, false, false, true);
    }

    private static GeneratorSettingBase overworld(boolean var0, boolean var1) {
        return new GeneratorSettingBase(new StructureSettings(true), NoiseSettings.create(-64, 384, new NoiseSamplingSettings(1.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-0.078125D, 2, var0 ? 0 : 8), new NoiseSlider(var0 ? 0.4D : 0.1171875D, 3, 0), 1, 2, false, var0, var1, TerrainProvider.overworld(var0)), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), SurfaceRuleData.overworld(), 63, false, true, true, true, true, false);
    }

    private static GeneratorSettingBase caves() {
        return new GeneratorSettingBase(new StructureSettings(false), NoiseSettings.create(-64, 192, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlider(0.9375D, 3, 0), new NoiseSlider(2.5D, 4, -1), 1, 2, false, false, false, TerrainProvider.caves()), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), SurfaceRuleData.overworldLike(false, true, true), 32, false, false, false, false, false, true);
    }

    private static GeneratorSettingBase floatingIslands() {
        return new GeneratorSettingBase(new StructureSettings(true), NoiseSettings.create(0, 256, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-23.4375D, 64, -46), new NoiseSlider(-0.234375D, 7, 1), 2, 1, false, false, false, TerrainProvider.floatingIslands()), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), SurfaceRuleData.overworldLike(false, false, false), -64, false, false, false, false, false, true);
    }

    static {
        CODEC = RegistryFileCodec.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, DIRECT_CODEC);
        OVERWORLD = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("overworld"));
        LARGE_BIOMES = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("large_biomes"));
        AMPLIFIED = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("amplified"));
        NETHER = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("nether"));
        END = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("end"));
        CAVES = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("caves"));
        FLOATING_ISLANDS = ResourceKey.create(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY, new MinecraftKey("floating_islands"));
        register(OVERWORLD, overworld(false, false));
        register(LARGE_BIOMES, overworld(false, true));
        register(AMPLIFIED, overworld(true, false));
        register(NETHER, nether());
        register(END, end());
        register(CAVES, caves());
        register(FLOATING_ISLANDS, floatingIslands());
    }
}
