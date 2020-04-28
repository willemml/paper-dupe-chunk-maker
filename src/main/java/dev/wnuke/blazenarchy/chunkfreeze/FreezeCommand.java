package dev.wnuke.blazenarchy.chunkfreeze;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;

import static dev.wnuke.blazenarchy.chunkfreeze.Chunkfreeze.VERSION;

public class FreezeCommand implements CommandExecutor {
    private ArrayList<Chunk> frozenChunks = new ArrayList<>();
    private void help(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "");
        sender.sendMessage(ChatColor.RED + "Please supply a valid argument: \"/freezechunk <help|freeze|version>\"");
        sender.sendMessage(ChatColor.RED + "  - help displays this text");
        sender.sendMessage(ChatColor.RED + "  - version displays the plugin's version");
        sender.sendMessage(ChatColor.RED + "  - freeze freezes the chunk you are in");
        sender.sendMessage(ChatColor.RED + "");
    }

    private void freezechunk(CommandSender sender) {
        Player player = (Player) sender;
        Chunk chunk = player.getChunk();
        if (frozenChunks.contains(chunk)) {
            sender.sendMessage(ChatColor.DARK_RED + "This chunk is already frozen.");
        } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Freezing chunk " + chunk.getX() + " " + chunk.getZ());
            frozenChunks.add(player.getChunk());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "version":
                    sender.sendMessage(ChatColor.AQUA + "Chunkfreeze plugin version " + VERSION + " by wnuke.");
                    return true;
                case "help":
                    help(sender);
                    return true;
                case "freeze":
                    sender.sendMessage("test");
                    if (sender instanceof Player) {
                        sender.sendMessage("test2");
                        freezechunk(sender);
                        return true;
                    }
                    sender.sendMessage(ChatColor.DARK_RED + "You must be a player to freeze a chunk.");
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

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (frozenChunks.contains(event.getChunk())) {
            if (event.isSaveChunk()) {
                event.setSaveChunk(false);
            }
        }
    }
}
