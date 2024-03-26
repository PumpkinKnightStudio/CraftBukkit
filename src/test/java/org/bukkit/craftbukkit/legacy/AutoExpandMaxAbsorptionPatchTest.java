package org.bukkit.craftbukkit.legacy;

import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeRanged;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.support.AbstractTestingBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AutoExpandMaxAbsorptionPatchTest extends AbstractTestingBase {

    private CraftAttributeInstance attributeInstance;

    @BeforeEach
    public void setup() {
        attributeInstance = new CraftAttributeInstance(new AttributeModifiable(new AttributeRanged("Test", 0, Integer.MIN_VALUE, Integer.MAX_VALUE), x -> { }), Attribute.GENERIC_MAX_ABSORPTION);
    }

    @Test
    public void testNoModifiers() {
        test(20);
    }

    @Test
    public void testSingleAddNumber() {
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Rouge", 10, AttributeModifier.Operation.ADD_NUMBER));

        test(20);
    }

    @Test
    public void testMultipleAddNumber() {
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Laputa", 10, AttributeModifier.Operation.ADD_NUMBER));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Poppy", 15.3, AttributeModifier.Operation.ADD_NUMBER));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Stone", -5, AttributeModifier.Operation.ADD_NUMBER));

        test(30);
    }

    @Test
    public void testSingleAddScalarWithBaseValue() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Service", 2, AttributeModifier.Operation.ADD_SCALAR));

        test(20);
    }

    @Test
    public void testMultipleAddScalarWithBaseValue() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "High", 2, AttributeModifier.Operation.ADD_SCALAR));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Auto", -3.14, AttributeModifier.Operation.ADD_SCALAR));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Iron", -0.5, AttributeModifier.Operation.ADD_SCALAR));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Gorilla", 0.23, AttributeModifier.Operation.ADD_SCALAR));

        test(20);
    }

    @Test
    public void testSingleMultiplyScalar1WithBaseValue() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Metallic", 2, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        test(20);
    }

    @Test
    public void testMultipleMultiplyScalar1WithBaseValue() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Entity", 2, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Resolver", -3.14, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Cookie", -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Layer", 0.23, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        test(20);
    }

    @Test
    public void testMixed() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Red", 10, AttributeModifier.Operation.ADD_NUMBER));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Green", 12.3, AttributeModifier.Operation.ADD_NUMBER));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Orange", 2, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "White", 2, AttributeModifier.Operation.ADD_SCALAR));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Yellow", 0.23, AttributeModifier.Operation.ADD_SCALAR));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Blue", -3.14, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "!", -0.14, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        test(99);
    }

    private void test(double target) {
        double toAdd = AutoExpandMaxAbsorptionPatch.calculateTarget(attributeInstance.getBaseValue(), target, attributeInstance.getModifiers());
        attributeInstance.addModifier(new AttributeModifier(UUID.randomUUID(), "Test", toAdd, AttributeModifier.Operation.ADD_NUMBER));

        assertEquals(target, attributeInstance.getValue(), 0.0001f);
    }
}
