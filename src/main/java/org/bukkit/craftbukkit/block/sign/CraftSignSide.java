package org.bukkit.craftbukkit.block.sign;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.entity.SignText;
import org.bukkit.DyeColor;
import org.bukkit.block.sign.SignSide;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftSignSide implements SignSide {

    private SignText signText;

    public CraftSignSide(SignText signText) {
        this.signText = signText;
    }

    @NotNull
    @Override
    public String[] getLines() {
        return toLegacyText(components.getLines());
    }

    private static String[] toLegacyText(BaseComponent[] components) {
        String[] lines = new String[components.length];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = BaseComponent.toLegacyText(components[i]);
        }
        return lines;
    }

    @NotNull
    @Override
    public String getLine(int index) throws IndexOutOfBoundsException {
        return BaseComponent.toLegacyText(components.getLine(index));
    }

    @Override
    public void setLine(int index, @NotNull String line) throws IndexOutOfBoundsException {
        this.components.setLine(index, TextComponent.fromLegacy(line));
    }

    @Override
    public boolean isGlowingText() {
        return signText.hasGlowingText();
    }

    @Override
    public void setGlowingText(boolean glowing) {
        signText = signText.setHasGlowingText(glowing);
    }

    @Nullable
    @Override
    public DyeColor getColor() {
        return DyeColor.getByWoolData((byte) signText.getColor().getId());
    }

    @Override
    public void setColor(@NotNull DyeColor color) {
        signText = signText.setColor(EnumColor.byId(color.getWoolData()));
    }

    public SignText applyToAndGetHandle() {
        if (components.lines != null) {
            for (int i = 0; i < components.lines.length; i++) {
                BaseComponent line = components.lines[i];
                signText = signText.setMessage(i, CraftChatMessage.fromBungeeOrNull(line));
            }
        }

        return signText;
    }

    private final CraftComponents components = new CraftComponents();

    public final class CraftComponents implements SignSide.Components {

        // Lazily initialized only if requested:
        private BaseComponent[] originalLines = null;
        private BaseComponent[] lines = null;

        @Override
        public BaseComponent[] getLines() {
            if (lines == null) {
                IChatBaseComponent[] messages = signText.getMessages(false);
                lines = new BaseComponent[messages.length];
                System.arraycopy(revertComponents(messages), 0, lines, 0, lines.length);
                originalLines = new BaseComponent[lines.length];
                System.arraycopy(lines, 0, originalLines, 0, originalLines.length);
            }
            return lines;
        }

        @Override
        public BaseComponent getLine(int index) throws IndexOutOfBoundsException {
            return getLines()[index];
        }

        @Override
        public void setLine(int index, BaseComponent line) throws IndexOutOfBoundsException {
            getLines()[index] = line;
        }

        private static BaseComponent[] revertComponents(IChatBaseComponent[] components) {
            BaseComponent[] lines = new BaseComponent[components.length];
            for (int i = 0; i < components.length; i++) {
                lines[i] = CraftChatMessage.toBungeeOrEmpty(components[i]);
            }
            return lines;
        }
    }

    @Override
    public Components components() {
        return components;
    }
}
