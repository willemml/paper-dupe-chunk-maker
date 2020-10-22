package net.willemml.chunkfreeze;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import static org.bukkit.Bukkit.getServer;

public class ChunkFreezer implements Listener {
    @EventHandler
    public void ChunkUnloadEvent(ChunkUnloadEvent event) {
        for (FrozenChunk frozenChunk : FreezeCommand.frozenChunks) {
            if (frozenChunk.chunk == event.getChunk()) {
                frozenChunk.sender.sendMessage(Chunkfreeze.PREFIX + "Chunk " + frozenChunk.chunk.getX() + " " + frozenChunk.chunk.getZ() + " was unloaded.");
                getServer().getLogger().info( Chunkfreeze.PREFIX + "Unloading chunk " + frozenChunk.chunk.getX() + " " + frozenChunk.chunk.getZ());
                event.setSaveChunk(false);
            }
        }
    }
}
