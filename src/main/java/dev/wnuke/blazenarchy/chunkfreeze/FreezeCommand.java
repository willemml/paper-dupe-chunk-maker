package dev.wnuke.blazenarchy.chunkfreeze;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Timer;

import static dev.wnuke.blazenarchy.chunkfreeze.Chunkfreeze.PREFIX;
import static dev.wnuke.blazenarchy.chunkfreeze.Chunkfreeze.VERSION;
import static org.bukkit.Bukkit.getServer;

public class FreezeCommand implements CommandExecutor {
    public static ArrayList<FrozenChunk> frozenChunks = new ArrayList<>();
    private void help(CommandSender sender) {
        sender.sendMessage(PREFIX + "Please supply a valid argument: \"/freezechunk <help|freeze|version>\"");
        sender.sendMessage(ChatColor.WHITE + "  - version displays the plugin's version");
        sender.sendMessage(ChatColor.WHITE + "  - freeze freezes the chunk you are in");
        sender.sendMessage(ChatColor.WHITE + "  - help tells you how to use this command");
    }

    private void freezechunk(CommandSender sender) {
        Player player = (Player) sender;
        Chunk chunk = player.getChunk();
        Boolean frozen = false;
        for (FrozenChunk frozenChunk : frozenChunks) {
            if (frozenChunk.sender == sender) {
                sender.sendMessage(PREFIX + "You have already frozen a chunk. (" + chunk.getX() + " " + chunk.getZ() + ")");
                frozen = true;
                break;
            }
            if (frozenChunk.chunk == chunk) {
                sender.sendMessage(PREFIX + "This chunk is already frozen. (by: " + frozenChunk.sender.getName() + " )");
                frozen = true;
                break;
            }
        }
        if (!frozen) {
            sender.sendMessage(PREFIX + "Freezing chunk " + chunk.getX() + " " + chunk.getZ() + ", it will unfreeze after 5 minutes");
            getServer().getLogger().info(PREFIX + sender.getName() + " froze chunk " + chunk.getX() + " " + chunk.getZ());
            frozenChunks.add(new FrozenChunk(player.getChunk(), sender));
            Timer t = new java.util.Timer();
            t.schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            unfreezechunk(sender, chunk, true);
                            t.cancel();
                        }
                    },
                    30000
            );
        }
    }

    private void unfreezechunk(CommandSender sender, Chunk chunk, Boolean auto) {
        Boolean isfrozen = false;
        for (FrozenChunk frozenChunk : frozenChunks) {
            if (frozenChunk.chunk == chunk) {
                isfrozen = true;
                sender.sendMessage(PREFIX + "Unfreezing chunk " + chunk.getX() + " " + chunk.getZ());
                getServer().getLogger().info(PREFIX + sender.getName() + " unfroze chunk " + chunk.getX() + " " + chunk.getZ());
                frozenChunks.remove(frozenChunk);
                break;
            }
        }
        if (!isfrozen && !auto) {
            sender.sendMessage(PREFIX + "Chunk " + chunk.getX() + " " + chunk.getZ() + " is not frozen.");
        }
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
                    sender.sendMessage(PREFIX + "You must be a player to freeze a chunk.");
                    return true;
                case "unfreeze":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Chunk chunk = player.getChunk();
                        unfreezechunk(sender, chunk, false);
                        return true;
                    }
                    sender.sendMessage(PREFIX + "You must be a player to unfreeze a chunk.");
                case "help":
                    sender.sendMessage(PREFIX + "How to use:");
                    sender.sendMessage("  1. Put everything you want to dupe in a chunk");
                    sender.sendMessage("  2. Wait for that chunk to unload and save by going 200 blocks away from it or to the nether");
                    sender.sendMessage("  3. Go back to the chunk and while in it use the freeze command");
                    sender.sendMessage("  4. Move the stuff you want to dupe out of the chunk");
                    sender.sendMessage("  5. Go away from it until you are told that it has unloaded");
                    sender.sendMessage("  6. Go back and get your stuff");
                    sender.sendMessage("  7. Done");
                    return true;
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
