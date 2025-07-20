package bwillows.treeregenerator.core;

import bwillows.treeregenerator.TreeRegenerator;
import bwillows.treeregenerator.model.ChunkLocation;
import bwillows.treeregenerator.model.ChunkTreeSnapshot;
import bwillows.treeregenerator.util.TreeUtils;
import bwillows.treeregenerator.util.Utils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
    public Data() {
        load();
    }

    public File dataFolder;
    public File worldsFolder;
    public File backupsFolder;

    public void load() {
        createDefaultFile();
    }

    public void save() {
        createDefaultFile();
    }

    public void createDefaultFile() {
        dataFolder = new File(TreeRegenerator.instance.pluginFolder, "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        worldsFolder = new File(dataFolder, "worlds");
        if (!worldsFolder.exists()) {
            worldsFolder.mkdirs();
        }
        backupsFolder = new File(dataFolder, "backups");
        if (!backupsFolder.exists()) {
            backupsFolder.mkdirs();
        }
    }

    public void saveChunkSnapshot(ChunkLocation chunkLocation) {
        if (chunkLocation == null || chunkLocation.chunk == null)
            return;

        World world = Bukkit.getWorld(chunkLocation.world);
        if (world == null)
            return;

        Bukkit.getScheduler().runTask(TreeRegenerator.instance, () -> {
            Chunk chunk = world.getChunkAt(chunkLocation.X, chunkLocation.Z);
            ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot(true, false, false);

            Bukkit.getScheduler().runTaskAsynchronously(TreeRegenerator.instance, () -> {
                if (!worldFolderExists(world)) {
                    createWorldFolder(world);
                    infoYml_create(world);
                } else {
                    if (!infoYml_validate(world)) {
                        createWorldBackup(world);
                        infoYml_create(world);
                    } else {
                        infoYml_update(world);
                    }
                }

                ChunkTreeSnapshot treeSnapshot = ChunkTreeSnapshot.fromSnapshot(chunkSnapshot);

            });
        });
    }

    public boolean worldFolderExists(World world) {
        if(world == null)
            return false;
        File worldFolder = new File(worldsFolder, world.getName());
        return worldFolder.exists();
    }
    public boolean createWorldFolder(World world) {
        if(world == null)
            return false;
        File worldFolder = new File(worldsFolder, world.getName());
        if (!worldFolder.exists()) {
            worldFolder.mkdirs();
        }
        return true;
    }

    public Set<String> backupsInProgress = ConcurrentHashMap.newKeySet();

    public void createWorldBackup(World world) {
        if(world == null)
            return;

        boolean added = backupsInProgress.add(world.getName());
        if (!added) return;

        File worldFolder = new File(worldsFolder, world.getName());
        if (!worldFolder.exists()) {
            return;
        }

        File worldTempFolder = new File(worldsFolder, world.getName() + "_TEMP");

        try {
            Utils.renameFolder(worldFolder.toPath(), worldTempFolder.toPath());
        } catch (Exception e) {

        }

        File worldBackup = new File(backupsFolder, world.getName() + "_" + Long.toString((System.currentTimeMillis() / 1000L)));
        if( !worldBackup.exists()) {
            worldBackup.mkdirs();
        }

        try {
            Utils.moveFolderAsync(worldTempFolder, worldBackup, true);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[TreeRegenerator] Failed to create backup for world: " + world.getName());
            e.printStackTrace();
        }
    }
    public boolean infoYml_create(World world) {
        if(world == null)
            return false;
        File worldFolder = new File(worldsFolder, world.getName());
        if (!worldFolder.exists()) {
            worldFolder.mkdirs();
        }
        File infoFile = new File(worldFolder, "info.yml");
        if (!infoFile.exists()) {
            try {
                infoFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("[TreeRegenerator] Failed to create info.yml for world: " + world.getName());
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        FileConfiguration infoYml = YamlConfiguration.loadConfiguration(infoFile);
        infoYml.set("first-seen", System.currentTimeMillis() / 1000);
        infoYml.set("seed", world.getSeed());
        infoYml.set("generator", Utils.getGeneratorName(world));
        try {
            infoYml.save(infoFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[TreeRegenerator] Failed to save info.yml for world: " + world.getName());
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean infoYml_update(World world) {
        if(world == null)
            return false;
        File worldFolder = new File(worldsFolder, world.getName());
        if (!worldFolder.exists()) {
            worldFolder.mkdirs();
        }
        File infoFile = new File(worldFolder, "info.yml");
        if (!infoFile.exists()) {
            try {
                infoFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("[TreeRegenerator] Failed to create info.yml for world: " + world.getName());
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        FileConfiguration infoYml = YamlConfiguration.loadConfiguration(infoFile);
        infoYml.set("last-seen", System.currentTimeMillis() / 1000);
        return true;
    }
    public boolean infoYml_validate(World world) {
        if(world == null)
            return false;
        File worldFolder = new File(worldsFolder, world.getName());
        if (!worldFolder.exists()) {
            return false;
        }
        File infoFile = new File(worldFolder, "info.yml");
        if (!infoFile.exists()) {
            return false;
        }
        FileConfiguration infoYml = YamlConfiguration.loadConfiguration(infoFile);

        String oldGenerator = infoYml.getString("generator");
        Long oldSeed = infoYml.getLong("seed");

        String currentGenerator = Utils.getGeneratorName(world);
        Long currentSeed = world.getSeed();

        return oldGenerator.equals(currentGenerator) && oldSeed.equals(currentSeed);
    }
}
