package bwillows.treeregenerator;

import bwillows.treeregenerator.commands.TreeRegeneratorCommand;
import bwillows.treeregenerator.core.Data;
import bwillows.treeregenerator.core.Manager;
import bwillows.treeregenerator.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TreeRegenerator extends JavaPlugin {

    public static TreeRegenerator instance;
    public static String version = "Unknown";

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

        getCommand("treeregenerator").setExecutor(new TreeRegeneratorCommand());
    }

    @Override
    public void onDisable() {
        data.save();
    }
}
