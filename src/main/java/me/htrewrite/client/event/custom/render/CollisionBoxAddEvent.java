package me.htrewrite.client.event.custom.render;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class CollisionBoxAddEvent extends CustomEvent {
    public Block block;
    public BlockPos pos;
    public AxisAlignedBB box;
    public CollisionBoxAddEvent(Block block, BlockPos pos, AxisAlignedBB box) {
        super();

        this.block = block;
        this.pos = pos;
        this.box = box;
    }
}