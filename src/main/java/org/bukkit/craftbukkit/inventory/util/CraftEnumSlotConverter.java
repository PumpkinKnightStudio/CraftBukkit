package org.bukkit.craftbukkit.inventory.util;

import net.minecraft.world.entity.EnumItemSlot;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A utility class designed to allow easy conversion between indexes, nms item slots and the bukkit variants
 */
public class CraftEnumSlotConverter {

    /**
     * Converts an {@link  EnumItemSlot} into its respective Bukkit {@link  EquipmentSlot} variant
     *
     * @param enumItemSlot The {@link EnumItemSlot} to convert
     * @return The {@link EquipmentSlot} matching the provided {@link  EnumItemSlot}
     */
    public static EquipmentSlot getAsBukkitSlot(EnumItemSlot enumItemSlot) {
        return EquipmentSlot.values()[enumItemSlot.ordinal()];
    }

    /**
     * Checks to see if the provided slot number is a valid index for an armor slot
     *
     * @param slot The slot number to check
     * @return {@code true} if the provided number is a valid index
     */
    public static boolean isEnumArmorSlot(int slot) {
        return slot >= 5 && slot <= 8;
    }

    /**
     * Converts a numerical index into the corresponding {@link EnumItemSlot} value
     *
     * @param slot The numerical index to get the corresponding {@link EnumItemSlot} value for
     * @return {@code null} if the numerical index is invalid (can be verified with {@link  #isEnumArmorSlot(int)}) or the
     * {@link EnumItemSlot} value that corresponds to the provided index
     */
    public static EnumItemSlot getFromEnumArmorSlot(int slot) {
        // There's no easy code in EnumItemSlot to convert from the number 7 for example to LEGS, so we are keeping this the only instance of hard coded checks
        EnumItemSlot enumItemSlot = null;
        if (slot == 5) {
            enumItemSlot = EnumItemSlot.HEAD;
        } else if (slot == 6) {
            enumItemSlot = EnumItemSlot.CHEST;
        } else if (slot == 7) {
            enumItemSlot = EnumItemSlot.LEGS;
        } else if (slot == 8) {
            enumItemSlot = EnumItemSlot.FEET;
        }

        return enumItemSlot;
    }
}
