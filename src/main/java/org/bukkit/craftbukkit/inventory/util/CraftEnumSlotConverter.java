package org.bukkit.craftbukkit.inventory.util;

import net.minecraft.world.entity.EnumItemSlot;
import org.bukkit.inventory.EquipmentSlot;

public class CraftEnumSlotConverter {

    public static EquipmentSlot getAsBucketSlot(EnumItemSlot enumItemSlot) {
        return EquipmentSlot.values()[enumItemSlot.ordinal()];
    }

    public static boolean isEnumArmorSlot(int slot) {
        return slot >= 5 && slot <= 8;
    }

    public static EnumItemSlot getFromEnumArmorSlot(int slot) {
        // There's no easy code in EnumItemSlot to convert from the number 7 for example to LEGS, so we are keeping this the only instance of hard coded checks
        EnumItemSlot enumItemSlot;
        if (slot == 5) {
            enumItemSlot = EnumItemSlot.HEAD;
        } else if (slot == 6) {
            enumItemSlot = EnumItemSlot.CHEST;
        } else if (slot == 7) {
            enumItemSlot = EnumItemSlot.LEGS;
        } else {
            enumItemSlot = EnumItemSlot.FEET;
        }

        return enumItemSlot;
    }
}
