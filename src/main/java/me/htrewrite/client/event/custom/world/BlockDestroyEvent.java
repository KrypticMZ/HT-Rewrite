package me.htrewrite.client.event.custom.world;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.util.math.BlockPos;

public class BlockDestroyEvent extends CustomEvent {
    private BlockPos blockPos;
    public BlockDestroyEvent(BlockPos blockPos) {
        super();
        this.blockPos = blockPos;
    }

    public void setBlockPos(BlockPos blockPos) { this.blockPos = blockPos; }
    public BlockPos getBlockPos() { return blockPos; }
}