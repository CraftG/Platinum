package xyz.hstudio.platinum;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import xyz.hstudio.hstudiolibrary.command.CmdUtils;
import xyz.hstudio.hstudiolibrary.yaml.Yaml;
import xyz.hstudio.platinum.board.AbstractBoard;
import xyz.hstudio.platinum.board.SideBoard;
import xyz.hstudio.platinum.board.TagBoard;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Platinum extends JavaPlugin implements Listener {

    private final List<SideBoard> sideBoardList = new ArrayList<>();
    private final List<TagBoard> tagBoardList = new ArrayList<>();
    private BukkitTask task;

    @Override
    public void onEnable() {
        load();
        Bukkit.getPluginManager().registerEvents(this, this);
        CmdUtils.register(this, new Commands(this), "platinum", "§9§lPlatinum §1§l>> §r§3没有找到此命令！");
    }

    public void load() {
        this.sideBoardList.clear();
        this.tagBoardList.clear();

        File folder = this.getDataFolder();
        if (!folder.exists()) {
            folder.mkdir();
        }

        {
            File[] sideboards = loadDir(folder, "sideboard");
            for (int i = 0; i < sideboards.length; i++) {
                sideBoardList.add(SideBoard.load(this,
                        Yaml.loadConfiguration(sideboards[i]), i));
            }
        }

        {
            File[] tagboards = loadDir(folder, "tagboard");
            for (int i = 0; i < tagboards.length; i++) {
                tagBoardList.add(TagBoard.load(this,
                        Yaml.loadConfiguration(tagboards[i]), i));
            }
        }

        Collections.sort(sideBoardList);
        Collections.sort(tagBoardList);

        if (this.task != null) {
            this.task.cancel();
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (SideBoard sideBoard : this.sideBoardList) {
                sideBoard.tick();
            }
            for (TagBoard tagBoard : this.tagBoardList) {
                tagBoard.tick();
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                this.sideBoardList
                        .stream()
                        .filter(board -> board.canView(p))
                        .findFirst()
                        .ifPresent(board -> board.update(p, p.getScoreboard()));

                this.tagBoardList
                        .stream()
                        .filter(board -> board.canView(p))
                        .findFirst()
                        .ifPresent(board -> board.update(p, p.getScoreboard()));
            }

            AbstractBoard.allTick++;
        }, 1L, 1L);
    }

    private File[] loadDir(final File folder, final String dir) {
        File file = new File(folder, dir);
        if (!file.isDirectory()) {
            saveResource(dir + "/example.yml", true);
        }
        return file.listFiles();
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}