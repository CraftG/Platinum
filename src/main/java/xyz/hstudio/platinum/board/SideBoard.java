package xyz.hstudio.platinum.board;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;
import xyz.hstudio.platinum.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SideBoard extends AbstractBoard {

    private final String[] header;
    private final String[][] lines;

    public SideBoard(final Plugin plugin, final int id, final String permission, final Set<String> world,
                     final int priority, final int update_tick, final String[] header, final String[][] lines) {
        super(id, permission, world, priority, update_tick);
        this.header = header;
        this.lines = lines;
    }

    @Override
    public void init(final Player player, final Scoreboard scoreboard) {
        Objective objective = scoreboard.registerNewObjective("sideBoard_" + this.id, "platinum");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(AbstractBoard.handle(player, this.header[0], 32));
        for (int line = 0; line < this.lines.length; line++) {
            String textLine = String.valueOf(line);
            String identifier = ChatColor.values()[line].toString();
            Team team = scoreboard.registerNewTeam("sideBoard_" + textLine);
            team.addEntry(identifier);
            Score score = objective.getScore(identifier);
            score.setScore(line);
            team.setPrefix(AbstractBoard.handle(player, this.lines[line][0], 32));
        }
    }

    @Override
    public void update(final Player player, final Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective("sideBoard_" + this.id);
        if (objective == null) {
            init(player, scoreboard);
            return;
        }
        if (allTick % this.update_tick == 0) {
            objective.setDisplayName(AbstractBoard.handle(player, this.header[this.tick % this.header.length], 32));
            for (int line = 0; line < this.lines.length; line++) {
                String textLine = String.valueOf(line);
                Team team = scoreboard.getTeam("sideBoard_" + textLine);
                team.setPrefix(AbstractBoard.handle(player, this.lines[line][this.tick % this.lines[line].length], 32));
            }
        }
    }

    public static SideBoard load(final Plugin plugin, final FileConfiguration config, final int id) {
        String permission = config.getString("permission");
        int priority = config.getInt("priority");
        int update_tick = config.getInt("update_tick", 20);
        Set<String> world = new HashSet<>(config.getStringList("world"));
        String[] header = config.getStringList("header").toArray(new String[0]);
        List<String[]> linesList = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("lines");
        if (section != null) {
            for (String line : section.getKeys(false)) {
                if (!StringUtils.isNumber(line)) {
                    continue;
                }
                linesList.add(config.isString("lines." + line) ?
                        new String[]{config.getString("lines." + line)} :
                        config.getStringList("lines." + line).toArray(new String[0]));
            }
        }
        String[][] lines = linesList.toArray(new String[0][]);
        return new SideBoard(plugin, id, permission, world, priority, update_tick, header, lines);
    }
}