package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.entity.TileEntitySign;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class CraftSign extends CraftBlockEntityState<TileEntitySign> implements Sign {

    // Lazily initialized only if requested:
    private String[] originalLines = null;
    private String[] lines = null;

    public CraftSign(World world, TileEntitySign tileEntity) {
        super(world, tileEntity);
    }

    @Override
    public String[] getLines() {
        if (lines == null) {
            // Lazy initialization:
            TileEntitySign sign = this.getSnapshot();
            lines = new String[sign.messages.length];
            System.arraycopy(revertComponents(sign.messages), 0, lines, 0, lines.length);
            originalLines = new String[lines.length];
            System.arraycopy(lines, 0, originalLines, 0, originalLines.length);
        }
        return lines;
    }

    @Override
    public String getLine(int index) throws IndexOutOfBoundsException {
        return getLines()[index];
    }

    @Override
    public void setLine(int index, String line) throws IndexOutOfBoundsException {
        getLines()[index] = line;
    }

    @Override
    public boolean isEditable() {
        return getSnapshot().isEditable;
    }

    @Override
    public void setEditable(boolean editable) {
        getSnapshot().isEditable = editable;
    }

    @Override
    public boolean isGlowingText() {
        return getSnapshot().hasGlowingText();
    }

    @Override
    public void setGlowingText(boolean glowing) {
        getSnapshot().setHasGlowingText(glowing);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.getByWoolData((byte) getSnapshot().getColor().getId());
    }

    @Override
    public void setColor(DyeColor color) {
        getSnapshot().setColor(EnumColor.byId(color.getWoolData()));
    }

    @Override
    public void applyTo(TileEntitySign sign) {
        super.applyTo(sign);

        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                String line = (lines[i] == null) ? "" : lines[i];
                if (line.equals(originalLines[i])) {
                    continue; // The line contents are still the same, skip.
                }
                sign.setMessage(i, CraftChatMessage.fromString(line)[0]);
            }
        }
    }

    public static void openSign(Sign sign, Player player) {
        Preconditions.checkArgument(sign != null, "sign == null");
        Preconditions.checkArgument(sign.isPlaced(), "Sign must be placed");
        Preconditions.checkArgument(sign.getWorld() == player.getWorld(), "Sign must be in same world as Player");

        TileEntitySign handle = ((CraftSign) sign).getTileEntity();
        handle.isEditable = true;

        ((CraftPlayer) player).getHandle().openTextEdit(handle);
    }

    public static IChatBaseComponent[] sanitizeLines(String[] lines) {
        IChatBaseComponent[] components = new IChatBaseComponent[4];

        for (int i = 0; i < 4; i++) {
            if (i < lines.length && lines[i] != null) {
                components[i] = CraftChatMessage.fromString(lines[i])[0];
            } else {
                components[i] = IChatBaseComponent.empty();
            }
        }

        return components;
    }

    public static String[] revertComponents(IChatBaseComponent[] components) {
        String[] lines = new String[components.length];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = revertComponent(components[i]);
        }
        return lines;
    }

    private static String revertComponent(IChatBaseComponent component) {
        return CraftChatMessage.fromComponent(component);
    }
}
