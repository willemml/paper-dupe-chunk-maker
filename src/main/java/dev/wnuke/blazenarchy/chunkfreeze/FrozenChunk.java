package dev.wnuke.blazenarchy.chunkfreeze;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;

public class FrozenChunk {
    Chunk chunk;
    CommandSender sender;

    public FrozenChunk(Chunk chunk, CommandSender sender) {
        this.chunk = chunk;
        this.sender = sender;
    }
}
