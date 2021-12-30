package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.world.BlockLiquidCollisionBBEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class JesusModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", 0, BetterMode.construct("Vanilla", "NCP", "Trampoline"));
    public static final ValueSetting<Double> trampolineHeight = new ValueSetting<>("JumpHeight", 1.18d, 0d, 50d);

    public static final ValueSetting<Double> offset = new ValueSetting<>("Offset", .18d, 0d, .9d);

    @Override public String getMeta() { return mode.getValue(); }
    public JesusModule() {
        super("Jesus", "Walk on water.", ModuleType.Miscellaneous, 0);
        addOption(mode);
        addOption(trampolineHeight.setVisibility(v -> mode.getValue().contentEquals("Trampoline")));
        addOption(offset);
        endOption();
    }

    private boolean checkCollide() {
        if(mc.player.isSneaking() || (mc.player.getRidingEntity() != null && mc.player.getRidingEntity().fallDistance >= 3f) || mc.player.fallDistance >= 3f)
            return false;
        return true;
    }

    public boolean isInLiquid() {
        if(mc.player == null || mc.player.fallDistance >= 3f)
            return false;

        boolean inLiquid = false;
        final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
        int y = (int)bb.minY;
        for(int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++)
            for(int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if(!(block instanceof BlockAir)) {
                    if(!(block instanceof BlockLiquid))
                        return false;
                    inLiquid = true;
                }
            }
        return inLiquid;
    }

    public boolean isOnLiquid(double offset) {
        if(mc.player == null || mc.player.fallDistance >= 3f)
            return false;

        final AxisAlignedBB bb = mc.player.getRidingEntity() != null ?
                mc.player.getRidingEntity().getEntityBoundingBox().contract(0d, 0d, 0d).offset(0d, -offset, 0d) :
                mc.player.getEntityBoundingBox().contract(0d, 0d, 0d).offset(0d, -offset, 0d);
        boolean onLiquid = false;
        int y = (int)bb.minY;
        for(int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1d); x++)
            for(int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1d); z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if(block != Blocks.AIR) {
                    if(!(block instanceof BlockLiquid))
                        return false;
                    onLiquid = true;
                }
            }
        return onLiquid;
    }

    @EventHandler
    private Listener<BlockLiquidCollisionBBEvent> collisionBBEventListener = new Listener<>(event -> {
        if(mc.world == null || mc.player == null || !checkCollide() || mc.player.motionY >= .1f || !(event.blockPos.getY() < mc.player.posY - offset.getValue()))
            return;
        if(mc.player.getRidingEntity() == null)
            switch(mode.getValue()) {
                case "Trampoline":
                    event.axisAlignedBB = new AxisAlignedBB(0, 0, 0, 1, .9f, 1);
                    break;

                default:
                    event.axisAlignedBB = Block.FULL_BLOCK_AABB;
                    break;
            }
        else event.axisAlignedBB = new AxisAlignedBB(0, 0, 0, 1, 1 - offset.getValue(), 1);
        event.cancel();
    });

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(event.getEra() == CustomEvent.Era.PRE && event.getPacket() instanceof CPacketPlayer) {
            if(mode.getValue().contentEquals("Vanilla") || mc.player.getRidingEntity() != null || mc.gameSettings.keyBindJump.isKeyDown())
                return;
            final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            if(!isInLiquid() && isOnLiquid(offset.getValue()) && checkCollide() && mc.player.ticksExisted % 3 == 0)
                packet.y -= offset.getValue();
        }
    });
}