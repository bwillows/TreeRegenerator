package bwillows.treeregenerator.core;

import bwillows.treeregenerator.model.ChunkLocation;
import bwillows.treeregenerator.model.ChunkTreeSnapshot;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class Manager {
    // may not always be present for every chunk, backup tool
    public Map<ChunkLocation, ChunkTreeSnapshot> chunkTreeSnapshots = new HashMap<>();


    public boolean regenerateChunk(ChunkLocation chunkLocation) {
        return false;
    }


    public void addBroken(Block block) {

    }
}
