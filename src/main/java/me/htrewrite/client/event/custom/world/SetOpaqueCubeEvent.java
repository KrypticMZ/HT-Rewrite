package me.htrewrite.client.event.custom.world;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.util.math.BlockPos;

public class SetOpaqueCubeEvent extends CustomEvent {
    public BlockPos blockPos;
    public SetOpaqueCubeEvent(BlockPos blockPos) {
        super();
        this.blockPos = blockPos;

        setEra(Era.PRE);
    }
}