package me.htrewrite.client.event.custom.world;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class BlockLiquidCollisionBBEvent extends CustomEvent {
    public AxisAlignedBB axisAlignedBB;
    public BlockPos blockPos;

    public BlockLiquidCollisionBBEvent() { super(); }
    public BlockLiquidCollisionBBEvent(BlockPos blockPos) {
        super();
        this.blockPos = blockPos;
    }
}