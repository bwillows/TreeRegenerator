package bwillows.treeregenerator.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadListener implements Listener {
    @EventHandler
    public void ChunkLoadListener(ChunkLoadEvent event) {
        if(event.isNewChunk()) {
            return;
        }


    }
}
