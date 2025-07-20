package bwillows.treeregenerator.model;

import bwillows.treeregenerator.TreeRegenerator;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.lang.reflect.Method;

public class Log extends TreeBlock {
    public Material material;
    public int damage;
    public int axis;

    public Log(Material material, int durability, int axis) {
        this.material = material;
        this.damage = durability;
        this.axis = axis;
    }

    public static Log fromBlock(Block block) {
        Material type = block.getType();
        if (!type.name().contains("LOG")) return null;

        if (TreeRegenerator.IS_1_13) {
            try {
                Object blockData = block.getClass().getMethod("getBlockData").invoke(block);
                Class<?> orientableClass = Class.forName("org.bukkit.block.data.Orientable");

                if (!orientableClass.isInstance(blockData)) return null;

                Method getAxis = orientableClass.getMethod("getAxis");
                Object axisEnum = getAxis.invoke(blockData);

                String axisName = axisEnum.toString(); // "X", "Y", "Z"
                int axisBits;
                switch (axisName) {
                    case "X": axisBits = 0x4; break;
                    case "Z": axisBits = 0x8; break;
                    default:  axisBits = 0x0; break;
                }

                int durability = 0;
                String name = type.name();
                if (name.contains("SPRUCE")) durability = 1;
                else if (name.contains("BIRCH")) durability = 2;
                else if (name.contains("JUNGLE")) durability = 3;
                else if (name.contains("ACACIA")) durability = 0;
                else if (name.contains("DARK_OAK")) durability = 1;

                return new Log(type, durability, axisBits);
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        } else {
            byte data = block.getData();
            int durability = data & 0x3;
            int axisBits = data & 0xC;
            return new Log(type, durability, axisBits);
        }
    }

    public static void setBlock(Log log, Block block) {
        block.setType(log.material);

        if (TreeRegenerator.IS_1_13) {
            try {
                Object blockData = block.getClass().getMethod("getBlockData").invoke(block);
                Class<?> orientableClass = Class.forName("org.bukkit.block.data.Orientable");
                if (!orientableClass.isInstance(blockData)) return;

                Method setAxis = orientableClass.getMethod("setAxis", Class.forName("org.bukkit.Axis"));

                Object axisValue;
                switch (log.axis) {
                    case 0x4:
                        axisValue = Enum.valueOf((Class<Enum>) Class.forName("org.bukkit.Axis"), "X");
                        break;
                    case 0x8:
                        axisValue = Enum.valueOf((Class<Enum>) Class.forName("org.bukkit.Axis"), "Z");
                        break;
                    default:
                        axisValue = Enum.valueOf((Class<Enum>) Class.forName("org.bukkit.Axis"), "Y");
                        break;
                }

                setAxis.invoke(blockData, axisValue);
                block.getClass().getMethod("setBlockData", Class.forName("org.bukkit.block.data.BlockData")).invoke(block, blockData);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            byte data = (byte) (log.damage | log.axis);
            try {
                Method setDataMethod = Block.class.getMethod("setData", byte.class);
                setDataMethod.invoke(block, data);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static boolean isLog(Material material) {
        if (material == null) return false;

        if(material.name().contains("LOG")) return true;

        if (TreeRegenerator.IS_1_13) {
            return material.equals(Material.matchMaterial("OAK_LOG"))
                    || material.equals(Material.matchMaterial("SPRUCE_LOG"))
                    || material.equals(Material.matchMaterial("BIRCH_LOG"))
                    || material.equals(Material.matchMaterial("JUNGLE_LOG"))
                    || material.equals(Material.matchMaterial("ACACIA_LOG"))
                    || material.equals(Material.matchMaterial("DARK_OAK_LOG"));
        } else {
            return material.equals(Material.matchMaterial("LOG")) || material.equals(Material.matchMaterial("LOG_2")) || material.name().toUpperCase().contains("LOG");
        }
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }
}
