package dev.wnuke.blazenarchy.chunkfreeze;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Blazenarchy's ChunkFreeze. Stops a chunk from saving.
 *
 * @author wnuke
 */
public final class Chunkfreeze extends JavaPlugin {
    public static final String VERSION = "1.0.0";
    public static final String PREFIX = ChatColor.RED + "[" + ChatColor.AQUA + "ChunkFreeze" + ChatColor.RED + "]" + ChatColor.WHITE + " ";

    @Override
    public void onEnable() {
        this.getCommand("freezechunk").setExecutor(new FreezeCommand());
        getServer().getPluginManager().registerEvents(new ChunkFreezer(), this);
        getLogger().info(PREFIX + "Loaded ChunkFreeze version " + VERSION + " by wnuke.");
    }

    @Override
    public void onDisable() {
        getLogger().info(PREFIX + "Disabling ChunkFreeze...");
    }
}
