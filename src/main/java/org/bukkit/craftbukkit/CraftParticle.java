package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Vibration;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public abstract class CraftParticle<D> extends Particle<D> {

    private static int count = 0;

    public static Particle<?> minecraftToBukkit(net.minecraft.core.particles.Particle<?> minecraft) {
        Preconditions.checkArgument(minecraft != null);

        IRegistry<net.minecraft.core.particles.Particle<?>> registry = CraftRegistry.getMinecraftRegistry(Registries.PARTICLE_TYPE);
        Particle<?> bukkit = Registry.PARTICLE_TYPE.get(CraftNamespacedKey.fromMinecraft(registry.getResourceKey(minecraft).orElseThrow().location()));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static net.minecraft.core.particles.Particle<?> bukkitToMinecraft(Particle<?> bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return ((CraftParticle<?>) bukkit).getHandle();
    }

    public static <T> T convertLegacy(T object) {
        if (object instanceof MaterialData mat) {
            return (T) CraftBlockData.fromData(CraftMagicNumbers.getBlock(mat));
        }

        return object;
    }

    private final NamespacedKey key;
    private final net.minecraft.core.particles.Particle<?> particle;
    private final Class<D> clazz;
    private final String name;
    private final int ordinal;

    public CraftParticle(NamespacedKey key, net.minecraft.core.particles.Particle<?> particle, Class<D> clazz) {
        this.key = key;
        this.particle = particle;
        this.clazz = clazz;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive particle specific values.
        // Custom particles will return the key with namespace. For a plugin this should look than like a new particle
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase();
        } else {
            this.name = key.toString();
        }
        this.ordinal = count++;
    }

    public net.minecraft.core.particles.Particle<?> getHandle() {
        return particle;
    }

    public abstract ParticleParam createParticleParam(D data);

    @NotNull
    @Override
    public Class<D> getDataType() {
        return clazz;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public int compareTo(Particle particle) {
        return ordinal - particle.ordinal();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        // For backwards compatibility
        return name();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftParticle<?>)) {
            return false;
        }

        return getKey().equals(((Particle<?>) other).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    public static class CraftParticleRegistry extends CraftRegistry<Particle<?>, net.minecraft.core.particles.Particle<?>> {

        private static final Map<NamespacedKey, BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>>> PARTICLE_MAP = new HashMap<>();

        private static final BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> voidFunction = (name, particle) -> new CraftParticle<>(name, particle, Void.class) {
            @Override
            public ParticleParam createParticleParam(Void data) {
                return (ParticleType) getHandle();
            }
        };

        static {
            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> dustOptionsFunction = (name, particle) -> new CraftParticle<>(name, particle, DustOptions.class) {
                @Override
                public ParticleParam createParticleParam(DustOptions data) {
                    Color color = data.getColor();
                    return new ParticleParamRedstone(new Vector3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f), data.getSize());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> itemStackFunction = (name, particle) -> new CraftParticle<>(name, particle, ItemStack.class) {
                @Override
                public ParticleParam createParticleParam(ItemStack data) {
                    return new ParticleParamItem((net.minecraft.core.particles.Particle<ParticleParamItem>) getHandle(), CraftItemStack.asNMSCopy(data));
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> blockDataFunction = (name, particle) -> new CraftParticle<>(name, particle, BlockData.class) {
                @Override
                public ParticleParam createParticleParam(BlockData data) {
                    return new ParticleParamBlock((net.minecraft.core.particles.Particle<ParticleParamBlock>) getHandle(), ((CraftBlockData) data).getState());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> dustTransitionFunction = (name, particle) -> new CraftParticle<>(name, particle, DustTransition.class) {
                @Override
                public ParticleParam createParticleParam(DustTransition data) {
                    Color from = data.getColor();
                    Color to = data.getToColor();
                    return new DustColorTransitionOptions(new Vector3f(from.getRed() / 255.0f, from.getGreen() / 255.0f, from.getBlue() / 255.0f), new Vector3f(to.getRed() / 255.0f, to.getGreen() / 255.0f, to.getBlue() / 255.0f), data.getSize());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> vibrationFunction = (name, particle) -> new CraftParticle<>(name, particle, Vibration.class) {
                @Override
                public ParticleParam createParticleParam(Vibration data) {
                    PositionSource source;
                    if (data.getDestination() instanceof Vibration.Destination.BlockDestination) {
                        Location destination = ((Vibration.Destination.BlockDestination) data.getDestination()).getLocation();
                        source = new BlockPositionSource(CraftLocation.toBlockPosition(destination));
                    } else if (data.getDestination() instanceof Vibration.Destination.EntityDestination) {
                        Entity destination = ((CraftEntity) ((Vibration.Destination.EntityDestination) data.getDestination()).getEntity()).getHandle();
                        source = new EntityPositionSource(destination, destination.getEyeHeight());
                    } else {
                        throw new IllegalArgumentException("Unknown vibration destination " + data.getDestination());
                    }

                    return new VibrationParticleOption(source, data.getArrivalTime());
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> floatFunction = (name, particle) -> new CraftParticle<>(name, particle, Float.class) {
                @Override
                public ParticleParam createParticleParam(Float data) {
                    return new SculkChargeParticleOptions(data);
                }
            };

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> integerFunction = (name, particle) -> new CraftParticle<>(name, particle, Integer.class) {
                @Override
                public ParticleParam createParticleParam(Integer data) {
                    return new ShriekParticleOption(data);
                }
            };

            add("dust", dustOptionsFunction);
            add("item", itemStackFunction);
            add("block", blockDataFunction);
            add("falling_dust", blockDataFunction);
            add("dust_color_transition", dustTransitionFunction);
            add("vibration", vibrationFunction);
            add("sculk_charge", floatFunction);
            add("shriek", integerFunction);
            add("block_marker", blockDataFunction);
        }

        private static void add(String name, BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> function) {
            PARTICLE_MAP.put(NamespacedKey.fromString(name), function);
        }

        public CraftParticleRegistry(IRegistry<net.minecraft.core.particles.Particle<?>> minecraftRegistry) {
            super(Particle.class, minecraftRegistry, null);
        }

        @Override
        public Particle<?> createBukkit(NamespacedKey namespacedKey, net.minecraft.core.particles.Particle<?> particle) {
            if (particle == null) {
                return null;
            }

            BiFunction<NamespacedKey, net.minecraft.core.particles.Particle<?>, CraftParticle<?>> function = PARTICLE_MAP.getOrDefault(namespacedKey, voidFunction);

            return function.apply(namespacedKey, particle);
        }
    }
}
