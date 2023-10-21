package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;

public class CraftFrog extends CraftAnimals implements org.bukkit.entity.Frog {

    public CraftFrog(CraftServer server, Frog entity) {
        super(server, entity);
    }

    @Override
    public Frog getHandle() {
        return (Frog) entity;
    }

    @Override
    public String toString() {
        return "CraftFrog";
    }

    @Override
    public Entity getTongueTarget() {
        return getHandle().getTongueTarget().map(net.minecraft.world.entity.Entity::getBukkitEntity).orElse(null);
    }

    @Override
    public void setTongueTarget(Entity target) {
        if (target == null) {
            getHandle().eraseTongueTarget();
        } else {
            getHandle().setTongueTarget(((CraftEntity) target).getHandle());
        }
    }

    @Override
    public Variant getVariant() {
        return CraftVariant.minecraftToBukkit(getHandle().getVariant());
    }

    @Override
    public void setVariant(Variant variant) {
        Preconditions.checkArgument(variant != null, "variant");

        getHandle().setVariant(CraftVariant.bukkitToMinecraft(variant));
    }

    public static class CraftVariant extends Variant {
        private static int count = 0;

        public static Variant minecraftToBukkit(FrogVariant minecraft) {
            Preconditions.checkArgument(minecraft != null);

            IRegistry<FrogVariant> registry = CraftRegistry.getMinecraftRegistry(Registries.FROG_VARIANT);
            Variant bukkit = Registry.FROG_VARIANT.get(CraftNamespacedKey.fromMinecraft(registry.getResourceKey(minecraft).orElseThrow().location()));

            Preconditions.checkArgument(bukkit != null);

            return bukkit;
        }

        public static FrogVariant bukkitToMinecraft(Variant bukkit) {
            Preconditions.checkArgument(bukkit != null);

            return ((CraftVariant) bukkit).getHandle();
        }

        private final NamespacedKey key;
        private final FrogVariant frogVariant;
        private final String name;
        private final int ordinal;

        public CraftVariant(NamespacedKey key, FrogVariant frogVariant) {
            this.key = key;
            this.frogVariant = frogVariant;
            // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
            // in case plugins use for example the name as key in a config file to receive variant specific values.
            // Custom variants will return the key with namespace. For a plugin this should look than like a new variant
            // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
            if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
                this.name = key.getKey().toUpperCase();
            } else {
                this.name = key.toString();
            }
            this.ordinal = count++;
        }

        public FrogVariant getHandle() {
            return frogVariant;
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }

        @Override
        public int compareTo(Variant variant) {
            return ordinal - variant.ordinal();
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

            if (!(other instanceof CraftVariant)) {
                return false;
            }

            return getKey().equals(((Variant) other).getKey());
        }

        @Override
        public int hashCode() {
            return getKey().hashCode();
        }
    }
}
