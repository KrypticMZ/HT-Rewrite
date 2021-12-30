package me.htrewrite.client.module.modules.world;

import me.htrewrite.client.event.custom.player.PlayerMotionUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.salimports.util.BlockInteractionHelper;
import me.htrewrite.salimports.util.EntityUtil;
import me.htrewrite.salimports.util.PlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class LawnmowerModule extends Module {
    public static final ValueSetting<Double> radius = new ValueSetting<>("Radius", null, 4D, 0D, 10D);
    public static final ToggleableSetting flowers = new ToggleableSetting("Flowers", null, true);

    public LawnmowerModule() {
        super("Lawnmower", "Break lawn yes.", ModuleType.World, 0);
        addOption(radius);
        addOption(flowers);
        endOption();
    }

    @EventHandler
    private Listener<PlayerMotionUpdateEvent> motionUpdateEventListener = new Listener<>(event -> {
        BlockPos closestPos = BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), radius.getValue().floatValue(), radius.getValue().intValue(), false, true, 0).stream()
                .filter(pos -> isValidBlockPos(pos))
                .min(Comparator.comparing(pos -> EntityUtil.GetDistanceOfEntityToBlock(mc.player, pos)))
                .orElse(null);
        if(closestPos != null) {
            event.cancel();
            final double pos[] = EntityUtil.calculateLookAt(
                    closestPos.getX() + .5,
                    closestPos.getY() + .5,
                    closestPos.getZ() + .5,
                    mc.player
            );
            PlayerUtil.PacketFacePitchAndYaw((float)pos[1], (float)pos[0]);

            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.playerController.clickBlock(closestPos, EnumFacing.UP);
        }
    });

    private boolean isValidBlockPos(final BlockPos pos) {
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() instanceof BlockTallGrass || state.getBlock() instanceof BlockDoublePlant)
            return true;
        if (flowers.isEnabled() && state.getBlock() instanceof BlockFlower)
            return true;

        return false;
    }
}