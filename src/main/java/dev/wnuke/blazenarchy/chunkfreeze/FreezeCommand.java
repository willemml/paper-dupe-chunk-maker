package dev.wnuke.blazenarchy.chunkfreeze;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static dev.wnuke.blazenarchy.chunkfreeze.Chunkfreeze.PREFIX;
import static dev.wnuke.blazenarchy.chunkfreeze.Chunkfreeze.VERSION;
import static org.bukkit.Bukkit.getServer;

public class FreezeCommand implements CommandExecutor {
    public static ArrayList<FrozenChunk> frozenChunks = new ArrayList<>();
    private void help(CommandSender sender) {
        sender.sendMessage(PREFIX + "Please supply a valid argument: \"/freezechunk <help|freeze|version>\"");
        sender.sendMessage(ChatColor.WHITE + "  - version displays the plugin's version");
        sender.sendMessage(ChatColor.WHITE + "  - freeze freezes the chunk you are in");
        sender.sendMessage(ChatColor.WHITE + "");
    }

    private void freezechunk(CommandSender sender) {
        Player player = (Player) sender;
        Chunk chunk = player.getChunk();
        if (frozenChunks.contains(chunk)) {
            sender.sendMessage(PREFIX + "This chunk is already frozen.");
        } else {
            sender.sendMessage(PREFIX + "Freezing chunk " + chunk.getX() + " " + chunk.getZ());
            getServer().getLogger().info(PREFIX + sender.getName() + " froze chunk " + chunk.getX() + " " + chunk.getZ());
            frozenChunks.add(new FrozenChunk(player.getChunk(), sender));
        }
    }

    private void unfreezechunk(CommandSender sender) {
        Player player = (Player) sender;
        Chunk chunk = player.getChunk();
        sender.sendMessage(PREFIX + "Unfreezing chunk " + chunk.getX() + " " + chunk.getZ());
        getServer().getLogger().info(PREFIX + sender.getName() + " unfroze chunk " + chunk.getX() + " " + chunk.getZ());
        frozenChunks.remove(chunk);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "version":
                    sender.sendMessage(ChatColor.AQUA + "Chunkfreeze plugin version " + VERSION + " by wnuke.");
                    return true;
                case "freeze":
                    if (sender instanceof Player) {
                        freezechunk(sender);
                        return true;
                    }
                    sender.sendMessage(ChatColor.DARK_RED + "You must be a player to freeze a chunk.");
                    return true;
                case "unfreeze":
                    unfreezechunk(sender);
                default:
                    help(sender);
                    return true;
            }
        } else {
            help(sender);
            return true;
        }
    }
}
