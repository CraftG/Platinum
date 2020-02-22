package xyz.hstudio.platinum;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import xyz.hstudio.hstudiolibrary.command.annotation.Cmd;

@RequiredArgsConstructor
public class Commands {

    private final Platinum platinum;

    @Cmd(name = "reload")
    public void reload(final CommandSender sender, final String[] args) {
        if (!sender.hasPermission("platinum.command.reload")) {
            sender.sendMessage("§9§lPlatinum §1§l>> §r§3你没有此命令的权限！");
            return;
        }
        platinum.load();
        sender.sendMessage("§9§lPlatinum §1§l>> §r§3重载完成！");
    }

    @Cmd(name = "")
    public void main(final CommandSender sender, final String[] args) {
        sender.sendMessage("§9§lPlatinum §1§l>> §r§3Platinum by MrCraftGoo");
    }
}