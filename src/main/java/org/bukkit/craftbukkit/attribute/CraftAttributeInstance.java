package org.bukkit.craftbukkit.attribute;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.server.AttributeModifiable;
import net.minecraft.server.IAttribute;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

public class CraftAttributeInstance implements AttributeInstance {

    private final net.minecraft.server.AttributeModifiable handle;
    private final Attribute attribute;

    public CraftAttributeInstance(net.minecraft.server.AttributeInstance handle, Attribute attribute) {
        this.handle = (AttributeModifiable) handle;
        this.attribute = attribute;
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public double getBaseValue() {
        return handle.b();
    }

    @Override
    public void setBaseValue(double d) {
        // Don't call setValue as to not fire the event.
        handle.f = d;
        handle.f();
    }

    @Override
    public Collection<AttributeModifier> getModifiers() {
        List<AttributeModifier> result = new ArrayList<AttributeModifier>();
        for (net.minecraft.server.AttributeModifier nms : handle.c()) {
            result.add(convert(nms));
        }

        return result;
    }

    @Override
    public void addModifier(AttributeModifier modifier) {
        Preconditions.checkArgument(modifier != null, "modifier");
        handle.b(convert(modifier));
    }

    @Override
    public void removeModifier(AttributeModifier modifier) {
        Preconditions.checkArgument(modifier != null, "modifier");
        handle.c(convert(modifier));
    }

    @Override
    public double getValue() {
        return handle.getValue();
    }

    public static net.minecraft.server.AttributeModifier convert(AttributeModifier bukkit) {
        return new net.minecraft.server.AttributeModifier(bukkit.getUniqueId(), bukkit.getName(), bukkit.getAmount(), bukkit.getOperation().ordinal());
    }

    public static AttributeModifier convert(net.minecraft.server.AttributeModifier nms) {
        return new AttributeModifier(nms.a(), nms.b(), nms.d(), AttributeModifier.Operation.values()[nms.c()]);
    }

    public static Attribute forNMSAttribute(IAttribute attribute) {
        String[] s = attribute.getName().split(".");
        String name = s[s.length-1];
        char[] c = name.toCharArray();
        StringBuilder out = new StringBuilder();
        out.append("GENERIC_");
        for(int i = 0; i<name.length(); i++) {
            if(Character.isUpperCase(c[i])) {
                out.append("_");
            }
            out.append(Character.toUpperCase(c[i]));
        }
        return Attribute.valueOf(out.toString());
    }
}
