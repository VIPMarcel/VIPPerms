package vip.marcel.vipperms.spigot.vipperms.utils.helper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import vip.marcel.vipperms.spigot.vipperms.utils.entities.VIPScoreboardTeam;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardHelper {

    private Player player;

    private Map<Integer, String> entryColorCodes;

    private Scoreboard scoreboard;
    private Objective objective;

    public ScoreboardHelper(Player player) {
        this.player = player;
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        this.entryColorCodes = new ConcurrentHashMap<>();
        this.entryColorCodes.put(0, "§0");
        this.entryColorCodes.put(1, "§1");
        this.entryColorCodes.put(2, "§2");
        this.entryColorCodes.put(3, "§3");
        this.entryColorCodes.put(4, "§4");
        this.entryColorCodes.put(5, "§5");
        this.entryColorCodes.put(6, "§6");
        this.entryColorCodes.put(7, "§7");
        this.entryColorCodes.put(8, "§8");
        this.entryColorCodes.put(9, "§9");
        this.entryColorCodes.put(10, "§a");
        this.entryColorCodes.put(11, "§b");
        this.entryColorCodes.put(12, "§c");
        this.entryColorCodes.put(13, "§d");
        this.entryColorCodes.put(14, "§e");
        this.entryColorCodes.put(15, "§f");

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective("aaa", "bbb");
        this.objective.setRenderType(RenderType.INTEGER);
    }

    public ScoreboardHelper() {
    }

    public ScoreboardHelper setDisplaySlot(DisplaySlot displaySlot) {
        this.objective.setDisplaySlot(displaySlot);
        return this;
    }

    public ScoreboardHelper setDisplayName(String displayName) {
        this.objective.setDisplayName(displayName);
        return this;
    }

    public ScoreboardHelper addTeam(int sortId, String name, ChatColor color, String prefix, String suffix) {
        name = name.toLowerCase();

        Team team = this.scoreboard.registerNewTeam(sortId + "-" + name);
        team.setColor(color);
        team.setPrefix(prefix);
        team.setSuffix(suffix);

        return this;
    }

    public ScoreboardHelper addFixRow(int lineId, String text) {
        this.objective.getScore(text).setScore(lineId);
        return this;
    }

    public ScoreboardHelper addUpdateableRow(int lineId, String fix, String updateable) {
        Team team = this.scoreboard.registerNewTeam("x" + lineId);
        team.setPrefix(fix);
        team.setSuffix(updateable);
        team.addEntry(this.entryColorCodes.get(lineId));

        this.objective.getScore(this.entryColorCodes.get(lineId)).setScore(lineId);
        return this;
    }

    public ScoreboardHelper addClearRow(int lineId) {

        StringBuilder stringBuilder = new StringBuilder("§f§8");

        for(int i = 0; i < lineId; i++) {
            stringBuilder.append(" ");
        }

        this.objective.getScore(stringBuilder.toString()).setScore(lineId);
        return this;
    }

    public ScoreboardHelper addTeams(List<VIPScoreboardTeam> teams) {

        teams.forEach(team -> {

            int sortId = team.getSortId();
            String name = team.getName();
            String colorCode = team.getColorCode();
            ChatColor color = null;
            String prefix = team.getPrefix();
            String suffix = team.getSuffix();

            color = switch (colorCode) {
                case "§0" -> ChatColor.BLACK;
                case "§1" -> ChatColor.DARK_BLUE;
                case "§2" -> ChatColor.DARK_GREEN;
                case "§3" -> ChatColor.DARK_AQUA;
                case "§4" -> ChatColor.DARK_RED;
                case "§5" -> ChatColor.DARK_PURPLE;
                case "§6" -> ChatColor.GOLD;
                case "§7" -> ChatColor.GRAY;
                case "§8" -> ChatColor.DARK_GRAY;
                case "§9" -> ChatColor.BLUE;
                case "§a" -> ChatColor.GREEN;
                case "§b" -> ChatColor.AQUA;
                case "§c" -> ChatColor.RED;
                case "§d" -> ChatColor.LIGHT_PURPLE;
                case "§e" -> ChatColor.YELLOW;
                case "§f" -> ChatColor.WHITE;
                default -> ChatColor.RESET;
            };

            this.addTeam(sortId, name, color, prefix, suffix);
        });

        return this;
    }

    public void updateDisplayName(Player player, String updateDisplayName) {
        if(player.getScoreboard() == null)
            return;

        if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null)
            return;

        player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(updateDisplayName);
    }

    public void updateRow(Player player, int lineId, String update) {
        if(player.getScoreboard() == null)
            return;

        if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null)
            return;

        if(player.getScoreboard().getTeam("x" + lineId) == null)
            return;

        player.getScoreboard().getTeam("x" + lineId).setSuffix(update);
    }

    public void updatePlayerTeam(Player player, int sortId, String name, boolean updateDisplayName) {
        Scoreboard scoreboard = player.getScoreboard();

        scoreboard.getTeam(sortId + "-" + name.toLowerCase()).addPlayer(player);

        if(updateDisplayName)
            player.setDisplayName(scoreboard.getPlayerTeam(player).getPrefix() + player.getName());

        Bukkit.getServer().getOnlinePlayers().forEach(players -> {
            scoreboard.getTeam(players.getScoreboard().getPlayerTeam(players).getName()).addPlayer(players);

            players.getScoreboard().getTeam(sortId + "-" + name.toLowerCase()).addPlayer(player);
        });
    }

    public void build() {
        this.player.setScoreboard(this.scoreboard);
    }

}