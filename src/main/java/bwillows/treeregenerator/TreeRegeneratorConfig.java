package bwillows.treeregenerator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class TreeRegeneratorConfig {
    public TreeRegeneratorConfig() {
        reload();
    }

    private File configFile;
    private File langFile;

    public FileConfiguration configYml;
    public FileConfiguration langYml;

    public class Settings {
        public int regenerationDelay;
        public boolean preventIfPlayerBlocksNearby;
        public int playerBlockCheckRadius;
        public boolean disableWhileRegenerating;
        public int dataSaveInterval;
        public List<String> enabledWorlds;
        public List<String> enabledRegions;

        public class removeOldWorlds {
            public boolean enabled;
            public int worldMaxAgeSeconds;
        }

        public removeOldWorlds removeOldWorlds = new removeOldWorlds();

    }
    public Settings settings = new Settings();

    public void reload() {
        configFile = new File(TreeRegenerator.instance.pluginFolder, "config.yml");
        if (!configFile.exists()) {
            TreeRegenerator.instance.saveResource("config.yml", false);
        }
        langFile = new File(TreeRegenerator.instance.pluginFolder, "lang.yml");
        if (!langFile.exists()) {
            TreeRegenerator.instance.saveResource("lang.yml", false);
        }

        configYml = YamlConfiguration.loadConfiguration(configFile);
        langYml = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(langFile);

        settings.regenerationDelay = configYml.getInt("regeneration-delay");
        settings.preventIfPlayerBlocksNearby = configYml.getBoolean("prevent-if-player-blocks-nearby");
        settings.playerBlockCheckRadius = configYml.getInt("player-block-check-radius");
        settings.disableWhileRegenerating = configYml.getBoolean("disable-while-regenerating");
        settings.dataSaveInterval = configYml.getInt("data-save-interval");
        settings.enabledWorlds = configYml.getStringList("enabled-worlds");
        settings.enabledRegions = configYml.getStringList("enabled-regions");

        settings.removeOldWorlds.enabled = configYml.getBoolean("remove-old-worlds.enabled");
        settings.removeOldWorlds.worldMaxAgeSeconds = configYml.getInt("remove-old-worlds.world-max-age-seconds");
    }
}
