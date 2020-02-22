package xyz.hstudio.platinum.board;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractBoard implements Comparable<AbstractBoard> {

    public static long allTick;

    protected final int id;
    private final String permission;
    private final Set<String> world;
    private final int priority;
    protected final int update_tick;
    protected int tick;

    public boolean canView(final Player player) {
        return (this.permission == null || this.permission.isEmpty() || player.hasPermission(this.permission)) &&
                this.world.contains(player.getWorld().getName());
    }

    public void tick() {
        if (allTick % this.update_tick != 0) {
            return;
        }
        this.tick++;
    }

    public abstract void init(final Player player, final Scoreboard scoreboard);

    public abstract void update(final Player player, final Scoreboard scoreboard);

    @Override
    public int compareTo(final AbstractBoard o) {
        return Integer.compare(this.priority, o.priority);
    }

    public static String handle(final Player p, final String str, final int length) {
        String string = str.substring(0, Math.min(str.length(), length));
        return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(p, string));
    }
}