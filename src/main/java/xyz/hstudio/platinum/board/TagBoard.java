package xyz.hstudio.platinum.board;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import xyz.hstudio.platinum.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagBoard extends AbstractBoard {

    private final Tag[] tags;

    public TagBoard(final Plugin plugin, final int id, final String permission, final Set<String> world,
                    final int priority, final int update_tick, final Tag[] tags) {
        super(id, permission, world, priority, update_tick);
        this.tags = tags;
    }

    @Override
    public void init(final Player player, final Scoreboard scoreboard) {
        Objective objective = scoreboard.registerNewObjective("tagBoard_" + this.id, "platinum");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(AbstractBoard.handle(player, tags[0].text, 16));
    }

    @Override
    public void update(final Player player, final Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective("tagBoard_" + this.id);
        if (objective == null) {
            init(player, scoreboard);
            return;
        }
        if (allTick % this.update_tick == 0) {
            objective.setDisplayName(AbstractBoard.handle(player, this.tags[this.tick % this.tags.length].text, 16));
        }
        for (Player p : player.getWorld().getPlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            String string = AbstractBoard.handle(p, this.tags[this.tick % this.tags.length].score, Integer.MAX_VALUE);
            objective.getScore(p.getName()).setScore(StringUtils.isNumber(string) ?
                    (int) Double.parseDouble(string) : 0);
        }
    }

    public static TagBoard load(final Plugin plugin, final FileConfiguration config, final int id) {
        String permission = config.getString("permission");
        int priority = config.getInt("priority");
        int update_tick = config.getInt("update_tick", 20);
        Set<String> world = new HashSet<>(config.getStringList("world"));
        List<Tag> tagsList = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("tags");
        if (section != null) {
            for (String line : section.getKeys(false)) {
                if (!StringUtils.isNumber(line)) {
                    continue;
                }
                Tag tag = new Tag(config.getString("tags." + line + ".score", "0"),
                        config.getString("tags." + line + ".text", ""));
                tagsList.add(tag);
            }
        }
        Tag[] tags = tagsList.toArray(new Tag[0]);
        return new TagBoard(plugin, id, permission, world, priority, update_tick, tags);
    }

    @RequiredArgsConstructor
    private static class Tag {
        private final String score;
        private final String text;
    }
}