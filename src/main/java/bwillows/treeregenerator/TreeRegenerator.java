package bwillows.treeregenerator;

import bwillows.treeregenerator.commands.TreeRegeneratorCommand;
import bwillows.treeregenerator.core.Data;
import bwillows.treeregenerator.core.Manager;
import bwillows.treeregenerator.listeners.*;
import bwillows.treeregenerator.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TreeRegenerator extends JavaPlugin {

    public static TreeRegenerator instance;
    public static String version = "Unknown";

    /*
        1.13 (name flattening)
        1.14 (tree generation changes)
        1.18 (full worldgen overhaul)
     */

    public static boolean IS_1_14 = Utils.isVersionAtLeast1_14();
    public static boolean IS_1_13 = Utils.isVersionAtLeast1_13();
    public static boolean IS_1_18 = Utils.isVersionAtLeast1_18();

    public File pluginFolder;
    public TreeRegeneratorConfig treeRegeneratorConfig;

    public Manager manager;
    public Data data;

    @Override
    public void onEnable() {
        instance = this;

        Properties props = new Properties();
        try (InputStream in = getResource("version.properties")) {
            if (in != null) {
                props.load(in);
                this.version = props.getProperty("version");
            } else {
                Bukkit.getLogger().warning("[TreeRegenerator] version.properties not found in plugin jar.");
            }
        } catch (IOException exception) {
            Bukkit.getLogger().warning("[TreeRegenerator] Unhandled exception loading version info");
            exception.printStackTrace();
        }

        pluginFolder = new File(getDataFolder().getParent(), getDescription().getName());
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        treeRegeneratorConfig = new TreeRegeneratorConfig();
        manager = new Manager();
        data = new Data();

        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityExplodeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PistonListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkLoadListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkUnloadListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldLoadListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldUnloadListener(), this);

        getCommand("treeregenerator").setExecutor(new TreeRegeneratorCommand());
    }

    @Override
    public void onDisable() {
        data.save();
    }
}
