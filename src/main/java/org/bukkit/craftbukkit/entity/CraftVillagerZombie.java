package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.EntityZombieVillager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;

public class CraftVillagerZombie extends CraftZombie implements ZombieVillager {

    public CraftVillagerZombie(CraftServer server, EntityZombieVillager entity) {
        super(server, entity);
    }

    @Override
    public EntityZombieVillager getHandle() {
        return (EntityZombieVillager) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftVillagerZombie";
    }

    @Override
    public Villager.Profession getVillagerProfession() {
        return Villager.Profession.valueOf(getRegistryAccess().registryOrThrow(Registries.VILLAGER_PROFESSION).getKey(getHandle().getVillagerData().getProfession()).getPath().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerProfession(Villager.Profession profession) {
        Validate.notNull(profession);
        getHandle().setVillagerData(getHandle().getVillagerData().setProfession(getRegistryAccess().registryOrThrow(Registries.VILLAGER_PROFESSION).get(new MinecraftKey(profession.name().toLowerCase(Locale.ROOT)))));
    }

    @Override
    public Villager.Type getVillagerType() {
        return Villager.Type.valueOf(getRegistryAccess().registryOrThrow(Registries.VILLAGER_TYPE).getKey(getHandle().getVillagerData().getType()).getPath().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerType(Villager.Type type) {
        Validate.notNull(type);
        getHandle().setVillagerData(getHandle().getVillagerData().setType(getRegistryAccess().registryOrThrow(Registries.VILLAGER_TYPE).get(CraftNamespacedKey.toMinecraft(type.getKey()))));
    }

    @Override
    public boolean isConverting() {
        return getHandle().isConverting();
    }

    @Override
    public int getConversionTime() {
        Preconditions.checkState(isConverting(), "Entity not converting");

        return getHandle().villagerConversionTime;
    }

    @Override
    public void setConversionTime(int time) {
        if (time < 0) {
            getHandle().villagerConversionTime = -1;
            getHandle().getEntityData().set(EntityZombieVillager.DATA_CONVERTING_ID, false);
            getHandle().conversionStarter = null;
            getHandle().removeEffect(MobEffects.DAMAGE_BOOST, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.CONVERSION);
        } else {
            getHandle().startConverting((UUID) null, time);
        }
    }

    @Override
    public OfflinePlayer getConversionPlayer() {
        return (getHandle().conversionStarter == null) ? null : Bukkit.getOfflinePlayer(getHandle().conversionStarter);
    }

    @Override
    public void setConversionPlayer(OfflinePlayer conversionPlayer) {
        if (!this.isConverting()) return;
        getHandle().conversionStarter = (conversionPlayer == null) ? null : conversionPlayer.getUniqueId();
    }
}
