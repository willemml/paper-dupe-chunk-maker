package dev.wnuke.blazenarchy.chunkfreeze;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Blazenarchy's ChunkFreeze. Stops a chunk from saving.
 *
 * @author wnuke
 */
public final class Chunkfreeze extends JavaPlugin {
    public static final String VERSION = "1.0.0";

    @Override
    public void onEnable() {
        this.getCommand("freezechunk").setExecutor(new FreezeCommand());
        getLogger().info("Loaded ChunkFreeze version " + VERSION + " by wnuke.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling ChunkFreeze...");
    }
}
