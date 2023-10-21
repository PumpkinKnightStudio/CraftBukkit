package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.junit.jupiter.params.provider.Arguments;

public class ItemStackLeatherTest extends ItemStackTest {

    public static Stream<Arguments> data() {
        return StackProvider.compound(operators(), "%s %s", NAME_PARAMETER, ItemType.LEATHER_BOOTS, ItemType.LEATHER_CHESTPLATE, ItemType.LEATHER_HELMET, ItemType.LEATHER_LEGGINGS);
    }

    @SuppressWarnings("unchecked")
    static List<Object[]> operators() {
        return CompoundOperator.compound(
            Joiner.on('+'),
            NAME_PARAMETER,
            Long.parseLong("10", 2),
            ItemStackLoreEnchantmentTest.operators(),
            Arrays.asList(
                new Object[] {
                    new Operator() {
                        @Override
                        public ItemStack operate(ItemStack cleanStack) {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            meta.setColor(Color.FUCHSIA);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        @Override
                        public ItemStack operate(ItemStack cleanStack) {
                            return cleanStack;
                        }
                    },
                    "Color vs Null"
                },
                new Object[] {
                    new Operator() {
                        @Override
                        public ItemStack operate(ItemStack cleanStack) {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            meta.setColor(Color.GRAY);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        @Override
                        public ItemStack operate(ItemStack cleanStack) {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    "Color vs Blank"
                },
                new Object[] {
                    new Operator() {
                        @Override
                        public ItemStack operate(ItemStack cleanStack) {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            meta.setColor(Color.MAROON);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        @Override
                        public ItemStack operate(ItemStack cleanStack) {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            meta.setColor(Color.ORANGE);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        }
                    },
                    "Color vs Other"
                }
            )
        );
    }
}
