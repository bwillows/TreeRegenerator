package bwillows.treeregenerator.listeners;

import bwillows.treeregenerator.TreeRegenerator;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldUnloadListener implements Listener {
    @EventHandler
    public void WorldUnloadListener(WorldUnloadEvent event) {
        World world = event.getWorld();
        boolean worldFolderExists = TreeRegenerator.instance.data.worldFolderExists(world);
        if(worldFolderExists) {
            boolean valid = TreeRegenerator.instance.data.infoYml_validate(world);
            if(!valid) {
                TreeRegenerator.instance.data.createWorldBackup(world);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        boolean done = TreeRegenerator.instance.data.backupsInProgress.contains(world.getName());
                        if(done) {
                            TreeRegenerator.instance.data.createWorldFolder(world);
                            TreeRegenerator.instance.data.infoYml_create(world);
                            TreeRegenerator.instance.data.infoYml_update(world);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(TreeRegenerator.instance, 0L, 1L);
            } else {
                TreeRegenerator.instance.data.infoYml_update(world);
            }
        } else {
            TreeRegenerator.instance.data.createWorldFolder(world);
            TreeRegenerator.instance.data.infoYml_create(world);
        }
    }
}
