package me.htrewrite.client.module.modules.combat;

import me.htrewrite.ciruimports.BlockUtil;
import me.htrewrite.ciruimports.InventoryUtil;
import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class InstantBurrowModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", null, 0, BetterMode.construct("Obsidian", "EnderChest"));
    public static final ToggleableSetting rotate = new ToggleableSetting("Rotate", null, false);
    public static final ValueSetting<Double> offset = new ValueSetting<>("Offset", null, 7D, -20D, 20D);

    private BlockPos originalPos;
    private int oldSlot = -1;

    public InstantBurrowModule() {
        super("InstantBurrow", "Instant burrow.", ModuleType.Combat, 0);
        addOption(mode);
        addOption(rotate);
        addOption(offset);
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Block block = mode.getI()==0?Blocks.OBSIDIAN:Blocks.ENDER_CHEST;

        originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock().equals(block) ||
            intersectsWithEntity(this.originalPos)) {
            toggle();
            return;
        }

        oldSlot = mc.player.inventory.currentItem;
    }

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> clientTickEventListener = new Listener<>(event -> {
        Class<? extends Block> block = mode.getI()==0?BlockObsidian.class : BlockEnderChest.class;
        if (InventoryUtil.findHotbarBlock(block) == -1) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cCan't find blocks.")));
            toggle();
            return;
        }
        InventoryUtil.switchToSlot(InventoryUtil.findHotbarBlock(block), false);

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));

        BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.isEnabled(), true, false);

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValue(), mc.player.posZ, false));
        InventoryUtil.switchToSlot(oldSlot, false);
        toggle();
    });

    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity.equals(mc.player)) continue;
            if (entity instanceof EntityItem) continue;
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) return true;
        }
        return false;
    }
}