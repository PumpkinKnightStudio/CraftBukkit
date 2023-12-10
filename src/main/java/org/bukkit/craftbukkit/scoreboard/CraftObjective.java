package org.bukkit.craftbukkit.scoreboard;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardObjective;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;

final class CraftObjective extends CraftScoreboardComponent implements Objective {
    private final ScoreboardObjective objective;
    private final CraftCriteria criteria;

    CraftObjective(CraftScoreboard scoreboard, ScoreboardObjective objective) {
        super(scoreboard);
        this.objective = objective;
        this.criteria = CraftCriteria.getFromNMS(objective);
    }

    ScoreboardObjective getHandle() {
        return objective;
    }

    @Override
    public String getName() {
        checkState();

        return objective.getName();
    }

    @Override
    public String getDisplayName() {
        return BaseComponent.toLegacyText(components.getDisplayName());
    }

    @Override
    public void setDisplayName(String displayName) {
        this.components.setDisplayName((displayName != null) ? TextComponent.fromLegacy(displayName) : null);
    }

    @Override
    public String getCriteria() {
        checkState();

        return criteria.bukkitName;
    }

    @Override
    public Criteria getTrackedCriteria() {
        checkState();

        return criteria;
    }

    @Override
    public boolean isModifiable() {
        checkState();

        return !criteria.criteria.isReadOnly();
    }

    @Override
    public void setDisplaySlot(DisplaySlot slot) {
        CraftScoreboard scoreboard = checkState();
        Scoreboard board = scoreboard.board;
        ScoreboardObjective objective = this.objective;

        for (net.minecraft.world.scores.DisplaySlot i : net.minecraft.world.scores.DisplaySlot.values()) {
            if (board.getDisplayObjective(i) == objective) {
                board.setDisplayObjective(i, null);
            }
        }
        if (slot != null) {
            net.minecraft.world.scores.DisplaySlot slotNumber = CraftScoreboardTranslations.fromBukkitSlot(slot);
            board.setDisplayObjective(slotNumber, getHandle());
        }
    }

    @Override
    public DisplaySlot getDisplaySlot() {
        CraftScoreboard scoreboard = checkState();
        Scoreboard board = scoreboard.board;
        ScoreboardObjective objective = this.objective;

        for (net.minecraft.world.scores.DisplaySlot i : net.minecraft.world.scores.DisplaySlot.values()) {
            if (board.getDisplayObjective(i) == objective) {
                return CraftScoreboardTranslations.toBukkitSlot(i);
            }
        }
        return null;
    }

    @Override
    public void setRenderType(RenderType renderType) {
        Preconditions.checkArgument(renderType != null, "RenderType cannot be null");
        checkState();

        this.objective.setRenderType(CraftScoreboardTranslations.fromBukkitRender(renderType));
    }

    @Override
    public RenderType getRenderType() {
        checkState();

        return CraftScoreboardTranslations.toBukkitRender(this.objective.getRenderType());
    }

    @Override
    public Score getScore(OfflinePlayer player) {
        checkState();

        return new CraftScore(this, CraftScoreboard.getScoreHolder(player));
    }

    @Override
    public Score getScore(String entry) {
        Preconditions.checkArgument(entry != null, "Entry cannot be null");
        Preconditions.checkArgument(entry.length() <= Short.MAX_VALUE, "Score '" + entry + "' is longer than the limit of 32767 characters");
        checkState();

        return new CraftScore(this, CraftScoreboard.getScoreHolder(entry));
    }

    @Override
    public void unregister() {
        CraftScoreboard scoreboard = checkState();

        scoreboard.board.removeObjective(objective);
    }

    @Override
    CraftScoreboard checkState() {
        Preconditions.checkState(getScoreboard().board.getObjective(objective.getName()) != null, "Unregistered scoreboard component");

        return getScoreboard();
    }

    private final CraftComponents components = new CraftComponents();

    private final class CraftComponents implements Objective.Components {

        @Override
        public BaseComponent getDisplayName() {
            checkState();

            return CraftChatMessage.toBungee(objective.getDisplayName());
        }

        @Override
        public void setDisplayName(BaseComponent displayName) {
            Preconditions.checkArgument(displayName != null, "displayName cannot be null");

            checkState();
            objective.setDisplayName(CraftChatMessage.fromBungee(displayName));
        }
    }

    @Override
    public Components components() {
        return components;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CraftObjective other = (CraftObjective) obj;
        return !(this.objective != other.objective && (this.objective == null || !this.objective.equals(other.objective)));
    }

}
