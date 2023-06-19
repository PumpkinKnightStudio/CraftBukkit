package org.bukkit.enchantments;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentSlotType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class EnchantmentTargetTest extends AbstractTestingBase {

    @Test
    public void test() {
        for (EnchantmentSlotType nmsSlot : EnchantmentSlotType.values()) {
            EnchantmentTarget bukkitTarget;
            switch (nmsSlot) {
                case ARMOR_CHEST:
                    bukkitTarget = EnchantmentTarget.ARMOR_TORSO;
                    break;
                case DIGGER:
                    bukkitTarget = EnchantmentTarget.TOOL;
                    break;
                default:
                    bukkitTarget = EnchantmentTarget.valueOf(nmsSlot.name());
                    break;
            }

            Assert.assertNotNull("No bukkit target for slot " + nmsSlot, bukkitTarget);

            for (Item item : BuiltInRegistries.ITEM) {
                ItemType itemType = CraftItemType.minecraftToBukkit(item);

                boolean nms = nmsSlot.canEnchant(item);
                boolean bukkit = bukkitTarget.includes(itemType);

                Assert.assertEquals("Slot mismatch for " + bukkitTarget + " and " + itemType, nms, bukkit);
            }
        }
    }
}
