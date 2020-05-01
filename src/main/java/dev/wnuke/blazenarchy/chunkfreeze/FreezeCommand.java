package dev.wnuke.blazenarchy.chunkfreeze;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Timer;

import static dev.wnuke.blazenarchy.chunkfreeze.Chunkfreeze.*;
import static org.bukkit.Bukkit.getServer;

public class FreezeCommand implements CommandExecutor {
    public static ArrayList<FrozenChunk> frozenChunks = new ArrayList<>();
    private void help(CommandSender sender) {
        sender.sendMessage(PREFIX + "Please supply a valid argument: \"/freezechunk <help|freeze|version>\"");
        sender.sendMessage(ChatColor.WHITE + "  - " + ChatColor.GOLD + "version|v" + ChatColor.WHITE + " displays the plugin's version");
        sender.sendMessage(ChatColor.WHITE + "  - " + ChatColor.GOLD + "freeze|f" + ChatColor.WHITE + " freezes the chunk you are in");
        sender.sendMessage(ChatColor.WHITE + "  - " + ChatColor.GOLD + "unfreeze|uf" + ChatColor.WHITE + " unfreezes the chunk you are in");
        sender.sendMessage(ChatColor.WHITE + "  - " + ChatColor.GOLD + "help|h" + ChatColor.WHITE + " tells you how to use this command");
        sender.sendMessage(ChatColor.WHITE + "  - " + ChatColor.GOLD + "list|l" + ChatColor.WHITE + " lists frozen chunks");
        sender.sendMessage(ChatColor.WHITE + "  - " + ChatColor.GOLD + "info|i" + ChatColor.WHITE + " displays config");
    }

    private void freezechunk(CommandSender sender) {
        Player player = (Player) sender;
        Chunk chunk = player.getChunk();
        int spawnRadius = CONFIG.getInt("spawnRadius");
        boolean canfreeze = true;
        if (chunk.getZ() < spawnRadius / 16 && chunk.getZ() > -spawnRadius / 16 && chunk.getX() < spawnRadius / 16 && chunk.getX() > -spawnRadius / 16) {
            canfreeze = false;
            sender.sendMessage(PREFIX + "You cannot freeze a chunk within "  + spawnRadius + " blocks of 0 0 (spawn)");
        }
        for (FrozenChunk frozenChunk : frozenChunks) {
            if (frozenChunk.sender == sender) {
                sender.sendMessage(PREFIX + "You have already frozen a chunk. (" + chunk.getX() + " " + chunk.getZ() + ")");
                canfreeze = false;
                break;
            }
            if (frozenChunk.chunk == chunk) {
                sender.sendMessage(PREFIX + "This chunk is already frozen. (by: " + frozenChunk.sender.getName() + " )");
                canfreeze = false;
                break;
            }
        }
        int autoUnfreezeDelay = CONFIG.getInt("autoUnfreezeDelay");
        if (canfreeze) {
            sender.sendMessage(PREFIX + "Freezing chunk " + chunk.getX() + " " + chunk.getZ() + ", it will unfreeze after " + autoUnfreezeDelay/1000/60 + " minutes");
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
                    autoUnfreezeDelay
            );
        }
    }

    private void unfreezechunk(CommandSender sender, Chunk chunk, Boolean auto) {
        boolean isfrozen = false;
        for (FrozenChunk frozenChunk : frozenChunks) {
            if (frozenChunk.chunk == chunk) {
                isfrozen = true;
                sender.sendMessage(PREFIX + "Unfreezing chunk " + chunk.getX() + " " + chunk.getZ());
                if (auto) {
                    getServer().getLogger().info(PREFIX + "Chunk " + chunk.getX() + " " + chunk.getZ() + " frozen by " + sender.getName() + " was automatically unfrozen.");
                } else {
                    getServer().getLogger().info(PREFIX + sender.getName() + " unfroze chunk " + chunk.getX() + " " + chunk.getZ());
                }
                frozenChunks.remove(frozenChunk);
                break;
            }
        }
        if (!isfrozen && !auto) {
            sender.sendMessage(PREFIX + "Chunk " + chunk.getX() + " " + chunk.getZ() + " is not frozen.");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String @NotNull [] args) {
        String missingPerm = PREFIX + "You do not have permission to use this command.";
        if (args.length >= 1) {
            switch (args[0]) {
                case "v":
                case "version":
                    sender.sendMessage(ChatColor.AQUA + "Chunkfreeze plugin version " + VERSION + " by wnuke.");
                    break;
                case "f":
                case "freeze":
                    if (sender.hasPermission("dupechunk.freeze")) {
                        if (sender instanceof Player) {
                            freezechunk(sender);
                            break;
                        }
                        sender.sendMessage(PREFIX + "You must be a player to freeze a chunk.");
                        break;
                    }
                    break;
                case "uf":
                case "unfreeze":
                    if (sender.hasPermission("dupechunk.unfreeze")) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            Chunk chunk = player.getChunk();
                            unfreezechunk(sender, chunk, false);
                            break;
                        }
                        sender.sendMessage(PREFIX + "You must be a player to unfreeze a chunk.");
                    } else {
                        sender.sendMessage(missingPerm);
                    }
                    break;
                case "h":
                case "help":
                    if (sender.hasPermission("dupechunk.help")) {
                        sender.sendMessage(PREFIX + "How to use:");
                        sender.sendMessage("  1. Put everything you want to dupe in a chunk");
                        sender.sendMessage("  2. Wait for that chunk to unload and save by going 200 blocks away from it or to the nether");
                        sender.sendMessage("  3. Go back to the chunk and while in it use the freeze command");
                        sender.sendMessage("  4. Move the stuff you want to dupe out of the chunk");
                        sender.sendMessage("  5. Go away from it until you are told that it has unloaded");
                        sender.sendMessage("  6. Go back and get your stuff");
                        sender.sendMessage("  7. Done");
                    } else {
                        sender.sendMessage(missingPerm);
                    }
                    break;
                case "l":
                case "list":
                    if (sender.hasPermission("dupechunk.list")) {
                        if (!frozenChunks.isEmpty()) {
                            sender.sendMessage(PREFIX + "List of frozen chunks:");
                            for (FrozenChunk frozenChunk : frozenChunks) {
                                sender.sendMessage("Chunk " + frozenChunk.chunk.getX() + " " + frozenChunk.chunk.getZ() + " frozen by " + frozenChunk.sender.getName());
                            }
                        } else {
                            sender.sendMessage(PREFIX + "There are no frozen chunks.");
                        }
                    } else {
                        sender.sendMessage(missingPerm);
                    }
                    break;
                case "i":
                case "info":
                    if (sender.hasPermission("dupechunk.info")) {
                        sender.sendMessage(PREFIX + "Current settings:");
                        sender.sendMessage("  - Auto unfreeze delay: " + CONFIG.getInt("autoUnfreezeDelay"));
                        sender.sendMessage("  - Disabled radius: " + CONFIG.getInt("spawnRadius"));
                    } else {
                        sender.sendMessage(missingPerm);
                    }
                    break;
                default:
                    help(sender);
                    break;
            }
        } else {
            help(sender);
        }
        return true;
    }
}
