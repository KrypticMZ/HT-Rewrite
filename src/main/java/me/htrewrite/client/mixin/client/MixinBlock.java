package me.htrewrite.client.mixin.client;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.render.CollisionBoxAddEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Block.class)
public abstract class MixinBlock extends IForgeRegistryEntry.Impl<Block> {
    @Shadow
    public abstract void addCollisionBoxToList(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox);

    @Overwrite
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        AxisAlignedBB blockBox = state.getCollisionBoundingBox(worldIn, pos);

        CollisionBoxAddEvent event = new CollisionBoxAddEvent(state.getBlock(), pos, blockBox);
        HTRewrite.EVENT_BUS.post(event);
        if(event.isCancelled())
            return;
        addCollisionBoxToList(pos, entityBox, collidingBoxes, event.box);
    }
}