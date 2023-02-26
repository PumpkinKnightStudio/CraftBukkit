package org.bukkit.craftbukkit.attribute;

import com.google.common.base.Preconditions;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftAttributeMap implements Attributable {

    private final AttributeMapBase handle;

    public CraftAttributeMap(AttributeMapBase handle) {
        this.handle = handle;
    }

    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        Preconditions.checkArgument(attribute != null, "attribute");
        net.minecraft.world.entity.ai.attributes.AttributeModifiable nms = handle.getInstance(toMinecraft(((CraftServer) Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registries.ATTRIBUTE), attribute));

        return (nms == null) ? null : new CraftAttributeInstance(nms, attribute);
    }

    public static AttributeBase toMinecraft(IRegistry<AttributeBase> registry, Attribute attribute) {
        return registry.get(CraftNamespacedKey.toMinecraft(attribute.getKey()));
    }

    public static Attribute fromMinecraft(String nms) {
        return Registry.ATTRIBUTE.get(CraftNamespacedKey.fromString(nms));
    }
}
