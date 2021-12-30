package me.htrewrite.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockData {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public final BlockPos blockPos;
    public final EnumFacing enumFacing;
    public BlockData(BlockPos blockPos, EnumFacing enumFacing) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
    }

    public static BlockData getBlockData(BlockPos blockPos) {
        if(mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() != Blocks.AIR)
            return new BlockData(blockPos.add(0, -1, 0), EnumFacing.UP);
        if(mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() != Blocks.AIR)
            return new BlockData(blockPos.add(-1, 0, 0), EnumFacing.EAST);
        if(mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() != Blocks.AIR)
            return new BlockData(blockPos.add(1, 0, 0), EnumFacing.WEST);
        if(mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() != Blocks.AIR)
            return new BlockData(blockPos.add(0, 0, -1), EnumFacing.SOUTH);
        if(mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() != Blocks.AIR)
            return new BlockData(blockPos.add(0, 0, 1), EnumFacing.NORTH);
        return null;
    }
}