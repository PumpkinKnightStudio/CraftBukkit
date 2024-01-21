package org.bukkit.craftbukkit.scoreboard;

import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.scoreboard.format.CraftNumberFormat;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.format.NumberFormat;

/**
 * TL;DR: This class is special and lazily grabs a handle...
 * ...because a handle is a full fledged (I think permanent) hashMap for the associated name.
 * <p>
 * Also, as an added perk, a CraftScore will (intentionally) stay a valid reference so long as objective is valid.
 */
final class CraftScore implements Score {
    private final ScoreHolder entry;
    private final CraftObjective objective;

    CraftScore(CraftObjective objective, ScoreHolder entry) {
        this.objective = objective;
        this.entry = entry;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(entry.getScoreboardName());
    }

    @Override
    public String getEntry() {
        return entry.getScoreboardName();
    }

    @Override
    public Objective getObjective() {
        return objective;
    }

    @Override
    public int getScore() {
        Scoreboard board = objective.checkState().board;

        ReadOnlyScoreInfo score = board.getPlayerScoreInfo(entry, objective.getHandle());
        if (score != null) { // Lazy
            return score.value();
        }

        return 0; // Lazy
    }

    @Override
    public void setScore(int score) {
        objective.checkState().board.getOrCreatePlayerScore(entry, objective.getHandle()).set(score);
    }

    @Override
    public boolean isScoreSet() {
        Scoreboard board = objective.checkState().board;

        return board.getPlayerScoreInfo(entry, objective.getHandle()) != null;
    }

    @Override
    public void setNumberFormat(NumberFormat format) {
        Scoreboard board = objective.checkState().board;

        // If the format is null but no score exists, we don't have to do anything.
        if (format == null && board.getPlayerScoreInfo(entry, objective.getHandle()) == null) {
            return;
        }

        // If the score ISN'T null, we have no choice but to create the score
        ScoreAccess score = objective.checkState().board.getOrCreatePlayerScore(entry, objective.getHandle());
        score.numberFormatOverride((format != null) ? CraftNumberFormat.bukkitToMinecraft(format) : null);
    }

    @Override
    public NumberFormat getNumberFormat() {
        Scoreboard board = objective.checkState().board;

        ReadOnlyScoreInfo score = board.getPlayerScoreInfo(entry, objective.getHandle());
        if (score != null) { // Lazy
            net.minecraft.network.chat.numbers.NumberFormat nmsFormat = score.numberFormat();
            return (nmsFormat != null) ? CraftNumberFormat.minecraftToBukkit(nmsFormat) : null;
        }

        return null;
    }

    @Override
    public CraftScoreboard getScoreboard() {
        return objective.getScoreboard();
    }
}
