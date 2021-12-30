package me.htrewrite.client.event.custom.world;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockEvent extends CustomEvent {
    public BlockPos pos;
    public EnumFacing facing;
    public int stage;

    public BlockEvent(final int stage, final BlockPos pos, final EnumFacing facing) {
        super();
        this.pos = pos;
        this.facing = facing;
        this.stage = stage;
    }
}