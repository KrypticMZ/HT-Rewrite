package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.manager.FriendManager;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.exeterimports.mcapi.settings.StringSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Mouse;

public class MiddleClickModule extends Module {
    public static final ToggleableSetting pm = new ToggleableSetting("PM", null, false);
    public static final StringSetting pm_command = new StringSetting("Command", null, "/msg");
    public static final ToggleableSetting pm_add = new ToggleableSetting("OnAdd", null, true);
    public static final ToggleableSetting pm_rem = new ToggleableSetting("OnRemove", null, true);

    FriendManager friendManager;

    public MiddleClickModule() {
        super("MCF", "MiddleClickFriend.", ModuleType.Miscellaneous, 0);
        addOption(pm);
        addOption(pm_command.setVisibility(v -> pm.isEnabled()));
        addOption(pm_add.setVisibility(v -> pm.isEnabled()));
        addOption(pm_rem.setVisibility(v -> pm.isEnabled()));
        endOption();

        friendManager = HTRewrite.INSTANCE.getFriendManager();
    }

    private boolean clicked = false;

    @EventHandler
    private Listener<PlayerUpdateEvent> playerUpdateEventListener = new Listener<>(event -> {
        if (mc.currentScreen != null)
            return;
        if(!Mouse.isButtonDown(2)) {
            clicked = false;
            return;
        }

        if(!clicked) {
            clicked = true;

            final RayTraceResult result = mc.objectMouseOver;
            if (result == null || result.typeOfHit != RayTraceResult.Type.ENTITY)
                return;

            Entity entity = result.entityHit;
            if (entity == null || !(entity instanceof EntityPlayer))
                return;

            if(friendManager.isFriend(entity.getName())) {
                friendManager.remFriend(entity.getName());
                mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cRemoved friend &l'" + entity.getName() + "'")));
                if(pm.isEnabled() && pm_rem.isEnabled())
                    mc.player.connection.sendPacket(new CPacketChatMessage(pm_command.getValue() + " " + entity.getName() + " I removed you from my friends list thanks to HT+Rewrite!"));
                return;
            }
            friendManager.addFriend(entity.getName());
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&aAdded friend &l'" + entity.getName() + "'")));

            if(pm.isEnabled() && pm_add.isEnabled())
                mc.player.connection.sendPacket(new CPacketChatMessage(pm_command.getValue() + " " + entity.getName() + " I added you to my friends list thanks to HT+Rewrite!"));
        }
    });
}