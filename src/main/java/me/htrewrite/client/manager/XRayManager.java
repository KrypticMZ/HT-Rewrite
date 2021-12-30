package me.htrewrite.client.manager;

import me.htrewrite.client.util.ConfigUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import org.json.JSONArray;

import java.util.ArrayList;

public class XRayManager {
    private ConfigUtils blocksConfig;
    public ArrayList<Integer> blocks;

    public XRayManager() {
        this.blocksConfig = new ConfigUtils("blocks", "xray");
        this.blocks = new ArrayList<>();

        if(blocksConfig.get("blocks") == null) { /* Set defaults */
            blocks.add(Block.getIdFromBlock(Blocks.DIAMOND_ORE));
            blocks.add(Block.getIdFromBlock(Blocks.COAL_ORE));
            blocks.add(Block.getIdFromBlock(Blocks.EMERALD_ORE));
            blocks.add(Block.getIdFromBlock(Blocks.GOLD_ORE));
            blocks.add(Block.getIdFromBlock(Blocks.IRON_ORE));
            blocks.add(Block.getIdFromBlock(Blocks.LAPIS_ORE));
            blocks.add(Block.getIdFromBlock(Blocks.QUARTZ_ORE));
            blocks.add(Block.getIdFromBlock(Blocks.REDSTONE_ORE));
            blocks.add(Block.getIdFromBlock(Blocks.LIT_REDSTONE_ORE));
            blocks.add(Block.getIdFromBlock(Blocks.END_PORTAL_FRAME));
            blocks.add(Block.getIdFromBlock(Blocks.END_PORTAL));
            blocks.add(Block.getIdFromBlock(Blocks.CHEST));
            blocks.add(Block.getIdFromBlock(Blocks.ENDER_CHEST));
            blocks.add(Block.getIdFromBlock(Blocks.TRAPPED_CHEST));
            blocks.add(Block.getIdFromBlock(Blocks.WATER));
            blocks.add(Block.getIdFromBlock(Blocks.FLOWING_WATER));
            blocks.add(Block.getIdFromBlock(Blocks.LAVA));
            blocks.add(Block.getIdFromBlock(Blocks.FLOWING_LAVA));

            save();
            return;
        }

        JSONArray array = (JSONArray)blocksConfig.get("blocks");
        for(int i = 0; i < array.toList().size(); i++)
            blocks.add((int)array.get(i));
    }

    public void save() {
        JSONArray array = new JSONArray();
        for(int block : blocks)
            array.put(block);
        blocksConfig.set("blocks", array);
        blocksConfig.save();
    }

    public boolean isBlock(Block block) { return blocks.contains(Block.getIdFromBlock(block)); }

    public Block addBlock(Block block) { blocks.add(Block.getIdFromBlock(block)); return block; }
    public Block addBlock(int id) { return addBlock(Block.getBlockById(id)); }
    public Block addBlock(String reference) {
        if(reference.matches("\\d+")) /* ID */
            return addBlock(Integer.parseInt(reference));
        return addBlock(Block.getBlockFromName(reference));
    }

    public Block removeBlock(Block block) { blocks.remove(Block.getIdFromBlock(block)); return block; }
    public Block removeBlock(int id) { return removeBlock(Block.getBlockById(id)); }
    public Block removeBlock(String reference) {
        if(reference.matches("\\d+"))
            return removeBlock(Integer.parseInt(reference));
        return removeBlock(Block.getBlockFromName(reference));
    }
}