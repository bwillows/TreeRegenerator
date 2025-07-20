package bwillows.treeregenerator.listeners;

import bwillows.treeregenerator.TreeRegenerator;
import bwillows.treeregenerator.model.ChunkLocation;
import bwillows.treeregenerator.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunkLoadListener implements Listener {
    @EventHandler
    public void ChunkLoadListener(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        boolean newChunk = event.isNewChunk();

        ChunkLocation chunkLocation = ChunkLocation.fromChunk(chunk);
        chunkLocation.chunk = chunk;

        boolean worldFolderExists = TreeRegenerator.instance.data.worldFolderExists(world);

        if (!worldFolderExists) {
            TreeRegenerator.instance.data.createWorldFolder(world);
            TreeRegenerator.instance.data.infoYml_create(world);

            if (newChunk) {
                TreeRegenerator.instance.data.saveChunkSnapshot(chunkLocation);
            }

            return;
        }

        boolean valid = TreeRegenerator.instance.data.infoYml_validate(world);

        if (!valid) {
            TreeRegenerator.instance.data.createWorldBackup(world);

            if (!TreeRegenerator.instance.data.backupsInProgress.contains(world.getName())) {
                TreeRegenerator.instance.data.createWorldFolder(world);
                TreeRegenerator.instance.data.infoYml_create(world);

                if (newChunk) {
                    TreeRegenerator.instance.data.saveChunkSnapshot(chunkLocation);
                }
            }

            return;
        }

        TreeRegenerator.instance.data.infoYml_update(world);

        if (newChunk) {
            TreeRegenerator.instance.data.saveChunkSnapshot(chunkLocation);
        }
    }
}
