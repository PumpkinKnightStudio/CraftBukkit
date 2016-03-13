package org.bukkit;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import net.minecraft.server.AttributeRanged;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.attribute.CraftAttributeMap;
import org.junit.Test;

public class AttributeTest {
    @Test
    public void verifyAttributeConversion() {
        for(Attribute attribute : Attribute.values()) {
            AttributeRanged nms = new AttributeRanged(null, CraftAttributeMap.toMinecraft(attribute.name()), 0, 0, 0);
            assertThat(CraftAttributeInstance.forNMSAttribute(nms), is(attribute));
        }
    }

    @Test
    public void verifyModifierConversion() {
        AttributeModifier modifier = new AttributeModifier("TEST", 0, AttributeModifier.Operation.ADD_NUMBER);
        net.minecraft.server.AttributeModifier nms = CraftAttributeInstance.convert(modifier);
        assertThat(modifier, is(CraftAttributeInstance.convert(nms)));
    }
}
