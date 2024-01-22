package org.bukkit.craftbukkit.scoreboard.format;

import com.google.common.base.Preconditions;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.scoreboard.format.NumberFormat;
import org.bukkit.scoreboard.format.BlankNumberFormat;
import org.bukkit.scoreboard.format.FixedNumberFormat;
import org.bukkit.scoreboard.format.StyledNumberFormat;

public final class CraftNumberFormat {

    public static NumberFormat minecraftToBukkit(net.minecraft.network.chat.numbers.NumberFormat minecraft) {
        Preconditions.checkArgument(minecraft != null);

        if (minecraft instanceof BlankFormat) {
            return NumberFormat.blank();
        } else if (minecraft instanceof FixedFormat fixed) {
            return NumberFormat.fixed(CraftChatMessage.toBungee(fixed.value));
        } else if (minecraft instanceof StyledFormat styled) {
            return NumberFormat.styled(CraftChatMessage.toBukkit(styled.style));
        }

        throw new UnsupportedOperationException("Unsupported NumberFormat implementation (Minecraft -> Bukkit): " + minecraft.getClass().getName());
    }

    public static net.minecraft.network.chat.numbers.NumberFormat bukkitToMinecraft(NumberFormat bukkit) {
        Preconditions.checkArgument(bukkit != null);

        if (bukkit instanceof BlankNumberFormat) {
            return BlankFormat.INSTANCE;
        } else if (bukkit instanceof FixedNumberFormat fixed) {
            return new FixedFormat(CraftChatMessage.fromBungee(fixed.components().getText()));
        } else if (bukkit instanceof StyledNumberFormat styled) {
            return new StyledFormat(CraftChatMessage.toMinecraft(styled.getStyle()));
        }

        throw new UnsupportedOperationException("Unsupported NumberFormat implementation (Bukkit -> Minecraft): " + bukkit.getClass().getName());
    }

    private CraftNumberFormat() {
    }

}
