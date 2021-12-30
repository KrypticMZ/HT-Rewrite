package me.htrewrite.client.event.custom.world;

import me.htrewrite.client.event.custom.CustomEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ShouldSideBeRenderedEvent extends CustomEvent {
    public IBlockAccess blockAccess;
    public BlockPos pos;
    public EnumFacing facing;
    public CallbackInfoReturnable<Boolean> callbackInfoReturnable;

    public ShouldSideBeRenderedEvent(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        super();
        this.blockAccess = blockAccess;
        this.pos = pos;
        this.facing = facing;
        this.callbackInfoReturnable = callbackInfoReturnable;

        setEra(Era.PRE);
    }
}