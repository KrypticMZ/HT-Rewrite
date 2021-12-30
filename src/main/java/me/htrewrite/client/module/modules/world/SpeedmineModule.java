package me.htrewrite.client.module.modules.world;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.event.custom.render.RenderEvent;
import me.htrewrite.client.event.custom.world.BlockEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.oyveyimports.util.InventoryUtil;
import me.htrewrite.oyveyimports.util.RenderUtil;
import me.htrewrite.oyveyimports.util.Timer;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class SpeedmineModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", null, 0, BetterMode.construct("Packet", "Damage", "Instant"));
    public static final ValueSetting<Double> damage = new ValueSetting<>("Damage", null, .7D, 0D, 1D);
    public static final ToggleableSetting webSwitch = new ToggleableSetting("WebSwitch", null, false);
    public static final ToggleableSetting doubleBreak = new ToggleableSetting("DoubleBreak", null, false);
    public static final ToggleableSetting render = new ToggleableSetting("Render", null, false);
    public static final ToggleableSetting box = new ToggleableSetting("Box", null, false);
    public static final ToggleableSetting outline = new ToggleableSetting("Outline", null, false);
    public static final ValueSetting<Double> boxAlpha = new ValueSetting<>("BoxAlpha", null, 85D, 0D, 255D);
    public static final ValueSetting<Double> lineWidth = new ValueSetting<>("Width", null, 1D, 0D, 5D);
    private static SpeedmineModule INSTANCE;
    public BlockPos currentPos;
    public IBlockState currentBlockState;
    private Timer timer = null;

    public SpeedmineModule() {
        super("Speedmine", "PacketMine.", ModuleType.World, 0);
        addOption(mode);
        addOption(damage);
        addOption(webSwitch);
        addOption(doubleBreak);
        addOption(render);
        addOption(box);
        addOption(outline);
        addOption(boxAlpha);
        addOption(lineWidth);
        endOption();

        this.timer = new Timer();
        INSTANCE = this;
    }

    public static SpeedmineModule getInstance() {
        if (SpeedmineModule.INSTANCE == null) {
            SpeedmineModule.INSTANCE = new SpeedmineModule();
        }
        return SpeedmineModule.INSTANCE;
    }

    private boolean checkNull() { return (mc.player == null || mc.world == null); }

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> tickEventListener = new Listener<>(event -> {
        if(checkNull())
            return;

        if (this.currentPos != null) {
            if (!mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) || mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
                this.currentPos = null;
                this.currentBlockState = null;
            }
            else if (webSwitch.isEnabled() && this.currentBlockState.getBlock() == Blocks.WEB && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
            }
        }
    });

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(checkNull())
            return;
        mc.playerController.blockHitDelay = 0;
    });

    @EventHandler
    private Listener<RenderEvent> renderEventListener = new Listener<>(event -> {
        if (this.render.isEnabled() && this.currentPos != null && this.currentBlockState.getBlock() == Blocks.OBSIDIAN) {
            final Color color = new Color(this.timer.passedMs((int)(2000.0f * 20)) ? 0 : 255, this.timer.passedMs((int)(2000.0f * 20)) ? 255 : 0, 0, 255);
            RenderUtil.drawBoxESP(this.currentPos, color, false, color, this.lineWidth.getValue().floatValue(), this.outline.isEnabled(), this.box.isEnabled(), this.boxAlpha.getValue().intValue(), false);
        }
    });

    public boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f;
    }

    @EventHandler
    private Listener<BlockEvent> blockEventListener = new Listener<>(event -> {
        if(checkNull())
            return;

        if (event.stage == 3 && mc.playerController.curBlockDamageMP > 0.1f)
            mc.playerController.isHittingBlock = true;
        if (event.stage == 4) {
            if(canBreak(event.pos)) {
                mc.playerController.isHittingBlock = false;
                switch (this.mode.getValue()) {
                    case "Packet": {
                        if (this.currentPos == null) {
                            this.currentPos = event.pos;
                            this.currentBlockState = mc.world.getBlockState(this.currentPos);
                            this.timer.reset();
                        }
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        event.cancel();
                        break;
                    }
                    case "Damage": {
                        if (mc.playerController.curBlockDamageMP >= this.damage.getValue()) {
                            mc.playerController.curBlockDamageMP = 1.0f;
                            break;
                        }
                        break;
                    }
                    case "Instant": {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        mc.playerController.onPlayerDestroyBlock(event.pos);
                        mc.world.setBlockToAir(event.pos);
                        break;
                    }
                }
            }
            if (this.doubleBreak.isEnabled()) {
                final BlockPos above = event.pos.add(0, 1, 0);
                if (canBreak(above) && mc.player.getDistance((double)above.getX(), (double)above.getY(), (double)above.getZ()) <= 5.0) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
                    mc.playerController.onPlayerDestroyBlock(above);
                    mc.world.setBlockToAir(above);
                }
            }
        }
    });

    @Override
    public String getMeta() {
        return mode.getValue();
    }
}