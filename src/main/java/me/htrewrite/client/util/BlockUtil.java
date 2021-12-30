package me.htrewrite.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockUtil {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static BlockPos[] nearbyBlocks(EntityLivingBase entity, int distance) {
        List<BlockPos> blocks = new ArrayList<>();
        for(int x = (int)mc.player.posX - distance; x <= (int)mc.player.posX + distance; ++x)
            for(int z = (int)mc.player.posZ - distance; z <= (int)mc.player.posZ + distance; ++z) {
                int height = mc.world.getHeight(x, z);
                for(int y = 0; y <= height; ++y)
                    blocks.add(new BlockPos(x, y, z));
            }

        return blocks.toArray(new BlockPos[0]);
    }
}