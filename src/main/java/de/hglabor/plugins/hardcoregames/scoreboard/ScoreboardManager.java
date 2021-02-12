package de.hglabor.plugins.hardcoregames.scoreboard;

import de.hglabor.plugins.hardcoregames.game.GameStateManager;
import de.hglabor.plugins.hardcoregames.player.HGPlayer;
import de.hglabor.plugins.hardcoregames.player.PlayerList;
import de.hglabor.plugins.kitapi.config.KitApiConfig;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.kits.CopyCatKit;
import de.hglabor.plugins.kitapi.kit.kits.NoneKit;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardFactory;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class ScoreboardManager {
    private ScoreboardManager() {
    }

    public static void setBasicScoreboardLayout(ScoreboardPlayer scoreboardPlayer) {
        int kitAmount = KitApiConfig.getInstance().getInteger("kit.amount");
        int lowestPosition = 7;
        int highestPosition = lowestPosition + kitAmount;
        ScoreboardFactory.addEntry(scoreboardPlayer, "gameState", Localization.INSTANCE.getMessage(
                "scoreboard.gameState." + GameStateManager.INSTANCE.getPhase().getType().name().toLowerCase(),
                scoreboardPlayer.getLocale()), "",
                highestPosition + 3);
        ScoreboardFactory.addEntry(scoreboardPlayer, "gameStateTime", "00:00", "", highestPosition + 2);
        ScoreboardFactory.addEntry(scoreboardPlayer, String.valueOf(highestPosition + 1), "", "", highestPosition + 1);
        if (kitAmount == 1) {
            ScoreboardFactory.addEntry(scoreboardPlayer, "kitValue" + 1, "Kit: None", "", highestPosition);
        } else if (kitAmount > 1) {
            for (int i = highestPosition; i > lowestPosition; i--) {
                ScoreboardFactory.addEntry(scoreboardPlayer, "kitValue" + (i - lowestPosition), "Kit" + (i - lowestPosition) + ": None", "", i);
            }
        }
        ScoreboardFactory.addEntry(scoreboardPlayer, "killsValue", "Kills: 0", "", lowestPosition);
        ScoreboardFactory.addEntry(scoreboardPlayer, "6", "", "", 6);
        ScoreboardFactory.addEntry(scoreboardPlayer, "players", Localization.INSTANCE.getMessage("scoreboard.players", scoreboardPlayer.getLocale()), "", 5);
        int alivePlayerSize = PlayerList.INSTANCE.getAlivePlayers().size();
        ScoreboardFactory.addEntry(scoreboardPlayer, "playersValue", alivePlayerSize + "/" + alivePlayerSize, "", 4);
        ScoreboardFactory.addEntry(scoreboardPlayer, "3", "", "", 3);
    }

    public static void updateForEveryone(String timeString) {
        for (HGPlayer hgPlayer : PlayerList.INSTANCE.getPlayers()) {
            hgPlayer.getBukkitPlayer().ifPresent(player -> {
                ScoreboardFactory.updateEntry(hgPlayer, "playersValue", SPACE() + PlayerList.INSTANCE.getAlivePlayers().size() + "/" + Bukkit.getMaxPlayers(), "");
                ScoreboardFactory.updateEntry(hgPlayer, "killsValue", ChatColor.AQUA + "" + ChatColor.BOLD + "Kills: " + ChatColor.RESET + hgPlayer.getKills(), "");
                ScoreboardFactory.updateEntry(hgPlayer, "gameStateTime", timeString, "");
                ScoreboardFactory.updateEntry(hgPlayer, "gameState", Localization.INSTANCE.getMessage("scoreboard.gameState." + GameStateManager.INSTANCE.getPhase().getType().name().toLowerCase(), hgPlayer.getLocale()), "");

                boolean kitDisabled = hgPlayer.areKitsDisabled();

                //could possibly be none -> name check
                if (KitApiConfig.getInstance().getInteger("kit.amount") > 0) {
                    int index = 1;
                    for (AbstractKit kit : hgPlayer.getKits()) {
                        kit = NoneKit.getInstance();
                        if (kit.equals(CopyCatKit.INSTANCE)) {
                            AbstractKit copiedKit = hgPlayer.getKitAttribute(CopyCatKit.INSTANCE);
                            ScoreboardFactory.updateEntry(hgPlayer,
                                    "kitValue" + index, ChatColor.BLUE + "" + ChatColor.BOLD + "Kit" + (index == 1 ? "" : index) + ": " + ChatColor.RESET +
                                            (kitDisabled ? ChatColor.STRIKETHROUGH : ChatColor.RESET) + kit.getName() +
                                            "(" + (copiedKit != null ? ((AbstractKit) hgPlayer.getKitAttribute(CopyCatKit.INSTANCE)).getName() : "None") + ")", "");
                        } else {
                            ScoreboardFactory.updateEntry(hgPlayer, "kitValue" + index, ChatColor.BLUE + "" + ChatColor.BOLD + "Kit" + (KitApiConfig.getInstance().getInteger("kit.amount") == 1 ? "" : index) + ": " + ChatColor.RESET + (kitDisabled ? ChatColor.STRIKETHROUGH : ChatColor.RESET) + kit.getName(), "");
                        }
                        index++;
                    }
                }
            });
        }
    }

    private static String SPACE() {
        return " ";
    }
}
