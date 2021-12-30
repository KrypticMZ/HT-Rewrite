package me.htrewrite.client.event.custom.render;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class RenderBlockEvent extends CustomEvent {
    public IBlockState state;
    public BlockPos pos;
    public IBlockAccess blockAccess;
    public BufferBuilder bufferBuilderIn;

    public RenderBlockEvent(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn) {
        super();
        this.state = state;
        this.pos = pos;
        this.blockAccess = blockAccess;
        this.bufferBuilderIn = bufferBuilderIn;

        setEra(Era.PRE);
    }
}