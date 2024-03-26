package org.bukkit.craftbukkit.legacy;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeRanged;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.legacy.reroute.DoNotReroute;
import org.bukkit.craftbukkit.legacy.reroute.InjectPluginName;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.VisibleForTesting;

public class AutoExpandMaxAbsorptionPatch {

    private static final UUID MODIFIER_UUID = UUID.fromString("44657246-725a-46f6-a36b-657220777368");

    public static void setAbsorptionAmount(LivingEntity livingEntity, double amount, @InjectPluginName String pluginName) {
        Preconditions.checkArgument(amount >= 0 && Double.isFinite(amount), "amount < 0 or non-finite");
        Preconditions.checkArgument(amount <= ((AttributeRanged) GenericAttributes.MAX_ABSORPTION).getMaxValue(), String.format("Absorption value (%s) must not be bigger than %s", amount, ((AttributeRanged) GenericAttributes.MAX_ABSORPTION).getMaxValue()));
        CraftLivingEntity craftLiving = (CraftLivingEntity) livingEntity;

        AttributeInstance instance = craftLiving.getAttribute(Attribute.GENERIC_MAX_ABSORPTION);
        instance.getModifiers()
                .stream()
                .filter(a -> MODIFIER_UUID.equals(a.getUniqueId()))
                .forEach(instance::removeModifier);

        if (amount <= instance.getValue()) {
            craftLiving.setAbsorptionAmount(amount);
            return;
        }

        if (!((CraftServer) Bukkit.getServer()).autoExpandMaxAbsorption) {
            throw new IllegalArgumentException(String.format("""
                            Absorption value (%s) must be between 0 and %s.
                            Please inform the developer(s) of the plugin '%s' to set the `GENERIC_MAX_ABSORPTION` attribute before calling `LivingEntity#setAbsorptionAmount(double)`.

                            If you have an outdated plugin and cannot update it, you can set `settings.patch.auto-expand-max-absorption` to `true` in `bukkit.yml`.
                            Bukkit will then attempt to mitigate the issue. However, this should be considered a last resort or a temporary solution until you can update the plugin.
                            Note that performance and the behavior of other plugins may be affected.
                            This workaround is only available for plugins with an API version lower than %s.""",
                    amount, craftLiving.getHandle().getMaxAbsorption(), pluginName, ApiVersion.AUTO_EXPAND_MAX_ABSORPTION_PATCH));
        }

        AttributeModifier modifier = new AttributeModifier(MODIFIER_UUID, "Auto Expand Max Absorption Patch Modifier", calculateTarget(instance.getBaseValue(), amount, instance.getModifiers()), AttributeModifier.Operation.ADD_NUMBER);
        instance.addModifier(modifier);
        craftLiving.setAbsorptionAmount(instance.getValue()); // Use #getValue to account for floating point error in the calculation above
    }

    @DoNotReroute
    @VisibleForTesting
    public static double calculateTarget(double baseValue, double target, Collection<AttributeModifier> modifiers) {
        // Make sure we expand only to the necessary amount
        double addNumber = 0;
        double addScalar = 1;
        double multiplyScalar1 = 1;

        for (AttributeModifier modifier : modifiers) {
            switch (modifier.getOperation()) {
                case ADD_NUMBER -> {
                    addNumber += modifier.getAmount();
                }
                case ADD_SCALAR -> {
                    addScalar += modifier.getAmount();
                }
                case MULTIPLY_SCALAR_1 -> {
                    multiplyScalar1 *= (1 + modifier.getAmount());
                }
                default -> throw new IllegalStateException("Unexpected value: " + modifier.getOperation());
            }
        }

        target /= (multiplyScalar1 * addScalar);
        target -= addNumber;
        target -= baseValue;

        return target;
    }
}
