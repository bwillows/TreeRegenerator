package bwillows.treeregenerator.listeners;

import bwillows.treeregenerator.TreeRegenerator;
import bwillows.treeregenerator.core.Data;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldLoadListener implements Listener {
    @EventHandler
    public void WorldLoadListener(WorldLoadEvent event) {
        World world = event.getWorld();

        boolean folderExists = TreeRegenerator.instance.data.worldFolderExists(world);
        if (!folderExists) {
            TreeRegenerator.instance.data.createWorldFolder(world);
            TreeRegenerator.instance.data.infoYml_create(world);
        } else {
            boolean infoYmlValid = TreeRegenerator.instance.data.infoYml_validate(world);
            if (!infoYmlValid) {
                TreeRegenerator.instance.data.createWorldBackup(world);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        boolean done = TreeRegenerator.instance.data.backupsInProgress.contains(world.getName());
                        if(done) {
                            TreeRegenerator.instance.data.createWorldFolder(world);
                            TreeRegenerator.instance.data.infoYml_create(world);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(TreeRegenerator.instance, 0L, 1L);
            } else {
                TreeRegenerator.instance.data.infoYml_update(world);
            }
        }
    }
}
