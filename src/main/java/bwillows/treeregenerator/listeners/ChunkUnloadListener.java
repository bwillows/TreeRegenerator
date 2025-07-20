package bwillows.treeregenerator.listeners;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {
    @EventHandler
    public void ChunkUnloadListener(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

    }
}
