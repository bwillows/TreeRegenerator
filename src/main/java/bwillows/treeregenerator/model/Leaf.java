package bwillows.treeregenerator.model;

import bwillows.treeregenerator.TreeRegenerator;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.lang.reflect.Method;

public class Leaf extends TreeBlock {
    public Material material;
    public int damage;
    public boolean decayable;
    public boolean checkDecay;

    public Leaf(Material material, int durability, boolean decayable, boolean checkDecay) {
        this.material = material;
        this.damage = durability;
        this.decayable = decayable;
        this.checkDecay = checkDecay;
    }

    public static Leaf fromBlock(Block block) {
        Material type = block.getType();
        if (!isLeaf(type)) return null;

        if (TreeRegenerator.IS_1_13) {
            try {
                Object blockData = block.getClass().getMethod("getBlockData").invoke(block);
                Class<?> leavesClass = Class.forName("org.bukkit.block.data.type.Leaves");

                if (!leavesClass.isInstance(blockData)) return null;

                Method isPersistent = leavesClass.getMethod("isPersistent");
                boolean persistent = (boolean) isPersistent.invoke(blockData);
                boolean decayable = !persistent;
                boolean checkDecay = true; // not exposed, assumed true

                int durability = 0;
                String name = type.name();
                if (name.contains("SPRUCE")) durability = 1;
                else if (name.contains("BIRCH")) durability = 2;
                else if (name.contains("JUNGLE")) durability = 3;
                else if (name.contains("ACACIA")) durability = 0;
                else if (name.contains("DARK_OAK")) durability = 1;

                return new Leaf(type, durability, decayable, checkDecay);
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        } else {
            byte data = block.getData();
            int durability = data & 0x3;
            boolean decayable = (data & 0x4) == 0;
            boolean checkDecay = (data & 0x8) != 0;
            return new Leaf(type, durability, decayable, checkDecay);
        }
    }

    public static void setBlock(Leaf leaf, Block block) {
        block.setType(leaf.material);

        if (TreeRegenerator.IS_1_13) {
            try {
                Object blockData = block.getClass().getMethod("getBlockData").invoke(block);
                Class<?> leavesClass = Class.forName("org.bukkit.block.data.type.Leaves");
                if (!leavesClass.isInstance(blockData)) return;

                Method setPersistent = leavesClass.getMethod("setPersistent", boolean.class);
                setPersistent.invoke(blockData, !leaf.decayable);

                Method setBlockData = block.getClass().getMethod("setBlockData", Class.forName("org.bukkit.block.data.BlockData"));
                setBlockData.invoke(block, blockData);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            byte data = (byte) (leaf.damage & 0x3);
            if (!leaf.decayable) data |= 0x4;
            if (leaf.checkDecay) data |= 0x8;

            try {
                Method setDataMethod = Block.class.getMethod("setData", byte.class);
                setDataMethod.invoke(block, data);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static boolean isLeaf(Material material) {
        if (material == null) return false;

        if (TreeRegenerator.IS_1_13) {
            return material.equals(Material.matchMaterial("OAK_LEAVES"))
                    || material.equals(Material.matchMaterial("SPRUCE_LEAVES"))
                    || material.equals(Material.matchMaterial("BIRCH_LEAVES"))
                    || material.equals(Material.matchMaterial("JUNGLE_LEAVES"))
                    || material.equals(Material.matchMaterial("ACACIA_LEAVES"))
                    || material.equals(Material.matchMaterial("DARK_OAK_LEAVES"));
        } else {
            return material.equals(Material.matchMaterial("LEAVES")) || material.equals(Material.matchMaterial("LEAVES_2")) || material.name().contains("LEAVES");
        }
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }
}
