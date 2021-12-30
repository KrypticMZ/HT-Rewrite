package me.htrewrite.client.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.player.PlayerMotionUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.module.modules.render.CityESPModule;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.salimports.managers.BlockManager;
import me.htrewrite.salimports.util.EntityUtil;
import me.htrewrite.salimports.util.Pair;
import me.htrewrite.salimports.util.PlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;

public class AutoCityModule extends Module {
    public AutoCityModule() {
        super("AutoCity", "Auto mine stuff yes city.", ModuleType.Combat, 0);
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        final ArrayList<Pair<EntityPlayer, ArrayList<BlockPos>>> cityPlayers = CityESPModule.GetPlayersReadyToBeCitied();

        if (cityPlayers.isEmpty())
        {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cThere is no one to city!")));
            toggle();
            return;
        }

        EntityPlayer target = null;
        BlockPos targetBlock = null;
        double currDistance = 100;

        for (Pair<EntityPlayer, ArrayList<BlockPos>> pair : cityPlayers)
        {
            for (BlockPos pos : pair.getSecond())
            {
                if (targetBlock == null)
                {
                    target = pair.getFirst();
                    targetBlock = pos;
                    continue;
                }

                double dist = pos.getDistance(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());

                if (dist < currDistance)
                {
                    currDistance = dist;
                    targetBlock = pos;
                    target = pair.getFirst();
                }
            }
        }

        if (targetBlock == null) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&',  "&cCouldn't find any blocks to mine!")));
            toggle();
            return;
        }

        BlockManager.SetCurrentBlock(targetBlock);
        mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', ChatFormatting.LIGHT_PURPLE + "Attempting to mine a block by your target: " + ChatFormatting.RED + target.getName())));
    }

    @EventHandler
    private Listener<PlayerMotionUpdateEvent> motionUpdateEventListener = new Listener<>(event -> {
        if (event.getEra() != CustomEvent.Era.PRE)
            return;

        boolean hasPickaxe = mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE;

        if (!hasPickaxe) {
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() == Items.DIAMOND_PICKAXE)
                {
                    hasPickaxe = true;
                    mc.player.inventory.currentItem = i;
                    mc.playerController.updateController();
                    break;
                }
            }
        }

        if (!hasPickaxe) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cNo pickaxe!")));
            toggle();
            return;
        }

        BlockPos currBlock = BlockManager.GetCurrBlock();

        if (currBlock == null) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&aDone!")));
            toggle();
            return;
        }

        event.cancel();

        final double rotations[] =  EntityUtil.calculateLookAt(
                currBlock.getX() + 0.5,
                currBlock.getY() - 0.5,
                currBlock.getZ() + 0.5,
                mc.player);

        PlayerUtil.PacketFacePitchAndYaw((float)rotations[1], (float)rotations[0]);

        BlockManager.Update(3, false);
    });
}