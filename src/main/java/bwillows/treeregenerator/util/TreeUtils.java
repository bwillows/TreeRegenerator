package bwillows.treeregenerator.util;

import bwillows.treeregenerator.TreeRegenerator;
import bwillows.treeregenerator.model.Leaf;
import bwillows.treeregenerator.model.Log;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtils {
    public static Map<Location, Log> getLogsInChunk(Chunk chunk) {
        Map<Location, Log> result = new HashMap<>();

        int minY = 0;
        int maxY = TreeRegenerator.IS_1_13 ? chunk.getWorld().getMaxHeight() : 256;

        for (int x = 0; x < 16; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    Material type = block.getType();

                    if(Log.isLog(type)) {
                        result.put(block.getLocation(), Log.fromBlock(block));
                    }
                }
            }
        }

        return result;
    }
    public static Map<Location, Leaf> getLeavesInChunk(Chunk chunk) {
        Map<Location, Leaf> result = new HashMap<>();

        int minY = 0;
        int maxY = TreeRegenerator.IS_1_13 ? chunk.getWorld().getMaxHeight() : 256;

        for (int x = 0; x < 16; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    Material type = block.getType();
                    if(Leaf.isLeaf(type)) {
                        result.put(block.getLocation(), Leaf.fromBlock(block));
                    }
                }
            }
        }

        return result;
    }
}
