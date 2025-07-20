package bwillows.treeregenerator.util;

import bwillows.treeregenerator.TreeRegenerator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;

public class Utils {
    public static String generateRandomString(int length) {
        // Define the characters to choose from (letters + digits)
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a Random object
        Random random = new Random();

        // StringBuilder to hold the generated string
        StringBuilder stringBuilder = new StringBuilder(length);

        // Generate the random string
        for (int i = 0; i < length; i++) {
            // Pick a random index from the characters string
            int randomIndex = random.nextInt(characters.length());
            // Append the character at the random index to the result
            stringBuilder.append(characters.charAt(randomIndex));
        }

        // Return the generated string
        return stringBuilder.toString();
    }

    public static boolean isInventoryFull(Player player) {
        Inventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return false; // Found an empty slot
            }
        }

        return true; // No empty slots found
    }

    /**
     * Checks if the server is running on a version >= 1.14.
     *
     * @return true if the server version is 1.14 or higher, false otherwise
     */
    public static boolean isVersionAtLeast1_14() {
        // Get the full version string from Bukkit
        String version = Bukkit.getVersion();  // e.g. "git-PaperSpigot-445 (MC: 1.13.2)"

        // Extract the part after "MC: " and split on dots
        String[] versionParts = version.split("MC: ")[1].split("\\."); // ["1","13","2)"]

        try {
            int majorVersion = Integer.parseInt(versionParts[0]);  // e.g. 1
            int minorVersion = Integer.parseInt(versionParts[1]);  // e.g. 13

            // Return true if version is greater than 1, or exactly 1.14+
            return majorVersion > 1 || (majorVersion == 1 && minorVersion >= 14);
        } catch (Exception e) {
            // Fallback on parse error
            Bukkit.getLogger().warning("Failed to parse Minecraft version string: " + version);
            return false;
        }
    }

    /**
     * Checks if the server is running on a version >= 1.13.
     *
     * @return true if the server version is 1.13 or higher, false otherwise
     */
    public static boolean isVersionAtLeast1_13() {
        // Get the full version string from Bukkit
        String version = Bukkit.getVersion();  // e.g. "git-PaperSpigot-445 (MC: 1.13.2)"

        // Extract the part after "MC: " and split on dots
        String[] versionParts = version.split("MC: ")[1].split("\\."); // ["1","13","2)"]

        try {
            int majorVersion = Integer.parseInt(versionParts[0]);  // e.g. 1
            int minorVersion = Integer.parseInt(versionParts[1]);  // e.g. 13

            // Return true if version is greater than 1, or exactly 1.13+
            return majorVersion > 1 || (majorVersion == 1 && minorVersion >= 13);
        } catch (Exception e) {
            // Fallback on parse error
            Bukkit.getLogger().warning("Failed to parse Minecraft version string: " + version);
            return false;
        }
    }

    /**
     * Checks if the server is running on a version >= 1.18.
     *
     * @return true if the server version is 1.18 or higher, false otherwise
     */
    public static boolean isVersionAtLeast1_18() {
        String version = Bukkit.getVersion(); // e.g. "git-Paper-123 (MC: 1.20.1)"

        try {
            String[] versionParts = version.split("MC: ")[1].split("\\."); // ["1", "20", "1)"]
            int majorVersion = Integer.parseInt(versionParts[0].replaceAll("[^0-9]", ""));
            int minorVersion = Integer.parseInt(versionParts[1].replaceAll("[^0-9]", ""));

            return majorVersion > 1 || (majorVersion == 1 && minorVersion >= 18);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to parse Minecraft version string: " + version);
            return false;
        }
    }

    /**
     * Checks if the server is running on a version >= 1.9.
     *
     * @return true if the server version is 1.9 or higher, false otherwise
     */
    public static boolean isVersionAtLeast1_9() {
        // Get the full version string from Bukkit
        String version = Bukkit.getVersion();  // e.g. "git-PaperSpigot-445 (MC: 1.13.2)"

        // Extract the part after "MC: " and split on dots
        String[] versionParts = version.split("MC: ")[1].split("\\."); // ["1","13","2)"]

        try {
            int majorVersion = Integer.parseInt(versionParts[0].replaceAll("[^0-9]", ""));
            int minorVersion = Integer.parseInt(versionParts[1].replaceAll("[^0-9]", ""));

            // Return true if version is greater than 1, or exactly 1.9+
            return majorVersion > 1 || (majorVersion == 1 && minorVersion >= 9);
        } catch (Exception e) {
            // Fallback on parse error
            Bukkit.getLogger().warning("Failed to parse Minecraft version string: " + version);
            return false;
        }
    }


    public static String locationToString(Location loc) {
        return loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
    }

    public static Location locationFromString(String str, World world) {
        String[] parts = str.split(",");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid location string: " + str);
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);

        float yaw = parts.length >= 4 ? Float.parseFloat(parts[3]) : 0f;
        float pitch = parts.length >= 5 ? Float.parseFloat(parts[4]) : 0f;

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void renameFolder(Path source, Path target) throws Exception {
        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
    }

    public static String getGeneratorName(World world) {
        if (world == null) return "default";

        if (world.getGenerator() == null) {
            switch (world.getEnvironment()) {
                case NETHER:
                    return "NETHER";
                case THE_END:
                    return "END";
                case NORMAL:
                default:
                    return "NORMAL";
            }
        }

        // Custom generator
        String generatorName = world.getGenerator().getClass().getName();

        // Detect if it's a vanilla generator by class name (Spigot or CraftBukkit)
        if (generatorName.contains("net.minecraft.server") || generatorName.contains("org.bukkit.craftbukkit")) {
            switch (world.getEnvironment()) {
                case NETHER:
                    return "NETHER";
                case THE_END:
                    return "END";
                default:
                    return "NORMAL";
            }
        }

        return generatorName;
    }

    public static void moveFolderAsync(File sourceDir, File targetDir, boolean deleteSource) {
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            Bukkit.getLogger().warning("[TreeRegenerator] Source folder does not exist or is not a directory: " + sourceDir.getPath());
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Files.walkFileTree(sourceDir.toPath(), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            Path targetPath = targetDir.toPath().resolve(sourceDir.toPath().relativize(dir));
                            if (!Files.exists(targetPath)) {
                                Files.createDirectories(targetPath);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Path targetPath = targetDir.toPath().resolve(sourceDir.toPath().relativize(file));
                            Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                            return FileVisitResult.CONTINUE;
                        }
                    });

                    if (deleteSource) {
                        Files.walkFileTree(sourceDir.toPath(), new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                Files.deleteIfExists(file);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                Files.deleteIfExists(dir);
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }

                    Bukkit.getLogger().info("[TreeRegenerator] Moved contents from " + sourceDir.getPath() + " to " + targetDir.getPath());

                    // after done
                    Bukkit.getScheduler().runTask(TreeRegenerator.instance, () -> {
                        String folderName = sourceDir.getName();
                        String originalWorldName = folderName.contains("_") ?
                                folderName.substring(0, folderName.indexOf('_')) :
                                folderName.replaceAll("\\d+$", ""); // fallback if no underscore

                        TreeRegenerator.instance.data.backupsInProgress.remove(originalWorldName);
                    });

                } catch (IOException e) {
                    Bukkit.getLogger().severe("[TreeRegenerator] Failed to move folder asynchronously: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(TreeRegenerator.instance);
    }
    public static boolean isPopulated(Chunk chunk) {
        int minY = Utils.isVersionAtLeast1_18() ? chunk.getWorld().getMinHeight() : 0;
        int maxY = chunk.getWorld().getMaxHeight();

        try {
            for(int y = maxY - 1; y >= minY; y--) {
                if(!chunk.getBlock(0, y, 0).getType().equals(Material.AIR)) {
                    return true;
                }
            }

        } catch (Exception e) {

        }
        return false;
    }

}
