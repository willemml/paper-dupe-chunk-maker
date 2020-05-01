package dev.wnuke.blazenarchy.chunkfreeze;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Blazenarchy's ChunkFreeze. Stops a chunk from saving.
 *
 * @author wnuke
 */

public final class Chunkfreeze extends JavaPlugin implements Listener {
    public static final String VERSION = "1.0.0";
    public static final String PREFIX = ChatColor.RED + "[" + ChatColor.AQUA + "ChunkFreeze" + ChatColor.RED + "]" + ChatColor.WHITE + " ";
    public static FileConfiguration CONFIG;
    public static JavaPlugin chunkfreeze;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Objects.requireNonNull(this.getCommand("dupechunk")).setExecutor(new FreezeCommand());
        CONFIG = this.getConfig();
        chunkfreeze = this;
        getServer().getPluginManager().registerEvents(new ChunkFreezer(), this);
        getLogger().info(PREFIX + "Loaded ChunkDupe version " + VERSION + " by wnuke.");
    }

    @EventHandler
    public void ChunkUnloadEvent(ChunkUnloadEvent event) {
        for (FrozenChunk frozenChunk : FreezeCommand.frozenChunks) {
            if (frozenChunk.chunk == event.getChunk()) {
                frozenChunk.sender.sendMessage(PREFIX + "Chunk " + frozenChunk.chunk.getX() + " " + frozenChunk.chunk.getZ() + " was unloaded.");
                getServer().getLogger().info( PREFIX + "Unloading chunk " + frozenChunk.chunk.getX() + " " + frozenChunk.chunk.getZ());
                event.setSaveChunk(false);
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(PREFIX + "Disabling ChunkFreeze...");
    }
}
