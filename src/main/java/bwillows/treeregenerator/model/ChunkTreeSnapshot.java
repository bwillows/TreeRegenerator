package bwillows.treeregenerator.model;

import bwillows.treeregenerator.TreeRegenerator;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

// May contain blocks from villages, mineshafts, etc.
public class ChunkTreeSnapshot {
    public ChunkLocation chunkLocation;
    public Map<Location, TreeBlock> treeBlocks = new HashMap<>();

    public static ChunkTreeSnapshot fromSnapshot(ChunkSnapshot chunkSnapshot) {
        ChunkTreeSnapshot chunkTreeSnapshot = new ChunkTreeSnapshot();
        ChunkLocation chunkLocation = new ChunkLocation(chunkSnapshot.getWorldName(), chunkSnapshot.getX(), chunkSnapshot.getZ());
        chunkTreeSnapshot.chunkLocation = chunkLocation;

        int minY, maxY;
        try {
            minY = TreeRegenerator.IS_1_18 ? (int) ChunkSnapshot.class.getMethod("getMinimumHeight").invoke(chunkSnapshot) : 0;
            maxY = TreeRegenerator.IS_1_18 ? (int) ChunkSnapshot.class.getMethod("getMaxHeight").invoke(chunkSnapshot) : 256;
        } catch (Throwable t) {
            minY = 0;
            maxY = 256;
        }

        int chunkX = chunkSnapshot.getX();
        int chunkZ = chunkSnapshot.getZ();

        World world = Bukkit.getWorld(chunkSnapshot.getWorldName());

        if (world == null) return chunkTreeSnapshot;

        for(int x = 0; x < 16; x++) {
            for(int y = minY; y < maxY; y++) {
                for(int z = 0; z < 16; z++) {
                    Material type = null;

                    try {
                        if(TreeRegenerator.IS_1_13) {
                            Method method = ChunkSnapshot.class.getMethod("getBlockType", int.class, int.class, int.class);
                            type = (Material) method.invoke(chunkSnapshot, x, y, z);
                        } else {
                            int id = -1;
                            Method getTypeId = ChunkSnapshot.class.getMethod("getBlockTypeId", int.class, int.class, int.class);
                            id = (int) getTypeId.invoke(chunkSnapshot, x, y, z);
                            if(id != -1) {
                                Method getMaterial = Material.class.getMethod("getMaterial", int.class);
                                type = (Material) getMaterial.invoke(null, id);
                            }
                        }
                    } catch (Throwable t) {

                    }

                    if(type == null)
                        continue;

                    int globalX = (chunkX << 4) + x;
                    int globalZ = (chunkZ << 4) + z;

                    if(Log.isLog(type)) {
                        int damage = -1;
                        int axis = -1;

                        if (TreeRegenerator.IS_1_13) {
                            try {
                                Method getBlockDataMethod = ChunkSnapshot.class.getMethod("getBlockData", int.class, int.class, int.class);
                                Object blockData = getBlockDataMethod.invoke(chunkSnapshot, x, y, z);

                                Class<?> orientableClass = Class.forName("org.bukkit.block.data.Orientable");
                                if (orientableClass.isInstance(blockData)) {
                                    Method getAxis = orientableClass.getMethod("getAxis");
                                    Object axisEnum = getAxis.invoke(blockData);
                                    if (axisEnum != null) {
                                        String axisName = axisEnum.toString(); // "X", "Y", "Z"
                                        switch (axisName) {
                                            case "X": axis = 4; break;
                                            case "Y": axis = 0; break;
                                            case "Z": axis = 8; break;
                                            default: axis = -1;
                                        }
                                    }
                                }
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        } else {
                            try {
                                Method getBlockDataMethod = ChunkSnapshot.class.getMethod("getBlockData", int.class, int.class, int.class);
                                Object result = getBlockDataMethod.invoke(chunkSnapshot, x, y, z);
                                byte data = ((Number) result).byteValue(); // safe cast
                                damage = data;
                                axis = data & 0xC; // bits 2 and 3
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }

                        Log log = new Log(type, damage, axis);
                        chunkTreeSnapshot.treeBlocks.put(new Location(world, globalX, y, globalZ), log);
                    } else if(Leaf.isLeaf(type)) {
                        int damage = -1;
                        boolean decayable = false;
                        boolean checkDecay = false;

                        if (TreeRegenerator.IS_1_13) {
                            try {
                                Method getBlockDataMethod = ChunkSnapshot.class.getMethod("getBlockData", int.class, int.class, int.class);
                                Object blockData = getBlockDataMethod.invoke(chunkSnapshot, x, y, z);

                                Class<?> leavesClass = Class.forName("org.bukkit.block.data.Leaves");

                                if (leavesClass.isInstance(blockData)) {
                                    Method isPersistent = leavesClass.getMethod("isPersistent");
                                    Method isDecaying = leavesClass.getMethod("isDecaying");

                                    boolean persistent = (boolean) isPersistent.invoke(blockData);
                                    decayable = !persistent; // persistent=false → decayable=true

                                    checkDecay = (boolean) isDecaying.invoke(blockData);
                                }
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        } else {
                            try {
                                Method getBlockDataMethod = ChunkSnapshot.class.getMethod("getBlockData", int.class, int.class, int.class);
                                Object result = getBlockDataMethod.invoke(chunkSnapshot, x, y, z);
                                byte data = ((Number) result).byteValue(); // ✅ safe cast from Integer

                                damage = data;

                                // bit 2 = decayable (0 = true, 1 = false)
                                // bit 3 = checkDecay (0 = false, 1 = true)
                                decayable = (data & 0x4) == 0;
                                checkDecay = (data & 0x8) != 0;
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }

                        Leaf leaf = new Leaf(type, damage, decayable, checkDecay);
                        chunkTreeSnapshot.treeBlocks.put(new Location(world, globalX, y, globalZ), leaf);
                    }
                }
            }
        }



        return chunkTreeSnapshot;

        /*



        Method getBlockTypeMethod = null;
        try {
            getBlockTypeMethod = ChunkSnapshot.class.getMethod("getBlockType", int.class, int.class, int.class);
        } catch (NoSuchMethodException ignored) {}

        Method getBlockDataMethod;
        try {
            getBlockDataMethod = ChunkSnapshot.class.getMethod("getBlockData", int.class, int.class, int.class);
        } catch (Throwable t) {
            t.printStackTrace();
            return chunkTreeSnapshot;
        }

        int chunkX = chunkSnapshot.getX();
        int chunkZ = chunkSnapshot.getZ();

        World world = Bukkit.getWorld(chunkSnapshot.getWorldName());

        if (world == null) return chunkTreeSnapshot;



        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    Material type;
                    try {
                        type = getBlockTypeMethod != null ? (Material) getBlockTypeMethod.invoke(chunkSnapshot, x, y, z) : Material.AIR;
                    } catch (Throwable t) {
                        t.printStackTrace();
                        continue;
                    }

                    if(!type.equals(Material.AIR)) {
                        Bukkit.getLogger().info(type.name());
                    }

                    int globalX = (chunkX << 4) + x;
                    int globalZ = (chunkZ << 4) + z;

                    if (Leaf.isLeaf(type)) {
                        if (TreeRegenerator.IS_1_13) {
                            try {
                                Object blockData = getBlockDataMethod.invoke(chunkSnapshot, x, y, z);
                                String blockDataString = blockData.getClass().getMethod("getAsString").invoke(blockData).toString();

                                boolean decayable = blockDataString.contains("persistent=false");
                                boolean checkDecay = true;
                                int durability = type.name().contains("SPRUCE") ? 1
                                        : type.name().contains("BIRCH") ? 2
                                        : type.name().contains("JUNGLE") ? 3
                                        : type.name().contains("ACACIA") ? 0
                                        : type.name().contains("DARK_OAK") ? 1
                                        : 0;

                                Leaf leaf = new Leaf(type, durability, decayable, checkDecay);
                                chunkTreeSnapshot.treeBlocks.put(new Location(world, globalX, y, globalZ), leaf);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        } else {
                            try {
                                int data = (int) getBlockDataMethod.invoke(chunkSnapshot, x, y, z);
                                byte byteData = (byte) data;

                                int durability = byteData & 0x3;
                                boolean decayable = (byteData & 0x4) == 0;
                                boolean checkDecay = (byteData & 0x8) != 0;

                                Leaf leaf = new Leaf(type, durability, decayable, checkDecay);
                                chunkTreeSnapshot.treeBlocks.put(new Location(world, globalX, y, globalZ), leaf);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    } else if (Log.isLog(type)) {
                        if (TreeRegenerator.IS_1_13) {
                            try {
                                Object blockData = getBlockDataMethod.invoke(chunkSnapshot, x, y, z);
                                String blockDataString = blockData.getClass().getMethod("getAsString").invoke(blockData).toString();

                                int axis = blockDataString.contains("axis=x") ? 0x4
                                        : blockDataString.contains("axis=z") ? 0x8
                                        : 0x0;
                                int durability = type.name().contains("SPRUCE") ? 1
                                        : type.name().contains("BIRCH") ? 2
                                        : type.name().contains("JUNGLE") ? 3
                                        : type.name().contains("ACACIA") ? 0
                                        : type.name().contains("DARK_OAK") ? 1
                                        : 0;

                                Log log = new Log(type, durability, axis);
                                chunkTreeSnapshot.treeBlocks.put(new Location(world, globalX, y, globalZ), log);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        } else {
                            try {
                                int data = (int) getBlockDataMethod.invoke(chunkSnapshot, x, y, z);
                                byte byteData = (byte) data;

                                int durability = byteData & 0x3;
                                int axis = byteData & 0xC;

                                Log log = new Log(type, durability, axis);
                                chunkTreeSnapshot.treeBlocks.put(new Location(world, globalX, y, globalZ), log);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    }
                }
            }
        }


         */
    }



}
