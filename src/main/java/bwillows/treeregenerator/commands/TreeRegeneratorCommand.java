package bwillows.treeregenerator.commands;

import bwillows.treeregenerator.TreeRegenerator;
import bwillows.treeregenerator.model.ChunkLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TreeRegeneratorCommand implements CommandExecutor {
    private static final Logger log = LogManager.getLogger(TreeRegeneratorCommand.class);

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("treeregenerator.*")){
            String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("no-permission");
            if(message != null && !message.trim().isEmpty()) {
                message = ChatColor.translateAlternateColorCodes('&', message);
                sender.sendMessage(message);
            }
            return true;
        }
        if(args.length == 0) {
            List<String> message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getStringList("no-argument");
            if(message != null) {
                for(String line : message) {
                    if(line != null && !line.trim().isEmpty()) {
                        line = ChatColor.translateAlternateColorCodes('&', line);
                        line.replace("%version%", TreeRegenerator.version);
                        sender.sendMessage(line);
                    }
                }
            }
            return true;
        }
        switch(args[0].toLowerCase()) {
            case "regen":
                {
                    if(args.length == 1) {
                        if(!(sender instanceof Player)) {
                            String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("renen-subcommand-console-sender");
                            if(message != null && !message.trim().isEmpty()) {
                                message = ChatColor.translateAlternateColorCodes('&', message);
                                sender.sendMessage(message);
                            }
                            return true;
                        }

                        Location location = ((Player) sender).getLocation();
                        ChunkLocation chunkLocation = ChunkLocation.fromLocation(location);

                        boolean result = TreeRegenerator.instance.manager.regenerateChunk(chunkLocation);

                        if(result) {
                            String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("regen-success");
                            if(message != null && !message.trim().isEmpty()) {
                                message = ChatColor.translateAlternateColorCodes('&', message);
                                sender.sendMessage(message);
                            }
                        } else {
                            String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("regen-fail");
                            if(message != null && !message.trim().isEmpty()) {
                                message = ChatColor.translateAlternateColorCodes('&', message);
                                sender.sendMessage(message);
                            }
                        }

                        return true;
                    }

                    if(args.length > 1) {
                        String worldName = args[1];
                        World world = Bukkit.getWorld(worldName);

                        if(world == null) {
                            String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("invalid-world");
                            if(message != null && !message.trim().isEmpty()) {
                                message = ChatColor.translateAlternateColorCodes('&', message);
                                sender.sendMessage(message);
                            }
                            return true;
                        }

                        int X;
                        int Z;

                        try {
                            if(args.length > 2) {
                                X = Integer.parseInt(args[2]);
                            } else {
                                String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("invalid-format");
                                if(message != null && !message.trim().isEmpty()) {
                                    message = ChatColor.translateAlternateColorCodes('&', message);
                                    sender.sendMessage(message);
                                }
                                return true;
                            }
                            if(args.length > 3) {
                                Z = Integer.parseInt(args[3]);
                            } else {
                                String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("invalid-format");
                                if(message != null && !message.trim().isEmpty()) {
                                    message = ChatColor.translateAlternateColorCodes('&', message);
                                    sender.sendMessage(message);
                                }
                                return true;
                            }
                        } catch (NumberFormatException exception) {
                            String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("invalid-cord");
                            if(message != null && !message.trim().isEmpty()) {
                                message = ChatColor.translateAlternateColorCodes('&', message);
                                sender.sendMessage(message);
                            }
                            return true;
                        }

                        ChunkLocation chunkLocation = new ChunkLocation(worldName, X, Z);

                        boolean result = TreeRegenerator.instance.manager.regenerateChunk(chunkLocation);

                        if(result) {
                            String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("regen-success");
                            if(message != null && !message.trim().isEmpty()) {
                                message = ChatColor.translateAlternateColorCodes('&', message);
                                sender.sendMessage(message);
                            }
                        } else {
                            String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("regen-fail");
                            if(message != null && !message.trim().isEmpty()) {
                                message = ChatColor.translateAlternateColorCodes('&', message);
                                sender.sendMessage(message);
                            }
                        }
                        return true;
                    }
                }
            return true;
            case "help":
                {
                    List<String> message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getStringList("help");
                    if(message != null) {
                        for(String line : message) {
                            if(line != null && !line.trim().isEmpty()) {
                                line = ChatColor.translateAlternateColorCodes('&', line);
                                line = line.replace("%version%", TreeRegenerator.version);
                                sender.sendMessage(line);
                            }
                        }
                    }
                }
            return true;
            case "reload":
                {
                    TreeRegenerator.instance.treeRegeneratorConfig.reload();
                    String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("reload");
                    if(message != null && !message.trim().isEmpty()) {
                        message = ChatColor.translateAlternateColorCodes('&', message);
                        sender.sendMessage(message);
                    }
                }
            return true;
            case "ver":
            case "version":
                {
                    String message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getString("version");
                    if(message != null && !message.trim().isEmpty()) {
                        message = ChatColor.translateAlternateColorCodes('&', message);
                        message = message.replace("%version%", TreeRegenerator.version);
                        sender.sendMessage(message);
                    }
                }
            return true;
            default:
                {
                    List<String> message = TreeRegenerator.instance.treeRegeneratorConfig.langYml.getStringList("no-argument");
                    if(message != null) {
                        for(String line : message) {
                            if(line != null && !line.trim().isEmpty()) {
                                line = ChatColor.translateAlternateColorCodes('&', line);
                                line = line.replace("%version%", TreeRegenerator.version);
                                sender.sendMessage(line);
                            }
                        }
                    }
                }
            return true;
        }
    }
}
