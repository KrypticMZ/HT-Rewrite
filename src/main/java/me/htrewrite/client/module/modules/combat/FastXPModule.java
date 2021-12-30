package me.htrewrite.client.module.modules.combat;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.ItemsUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.Items;
import net.minecraft.item.ItemExpBottle;

public class FastXPModule extends Module {
    @Override public String getMeta() { return String.valueOf(ItemsUtil.getItemCount(Items.EXPERIENCE_BOTTLE)); }

    public FastXPModule() {
        super("FastXP", "Similar as FastPlace but only with XP.", ModuleType.Combat, 0);
        endOption();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.rightClickDelayTimer = 6;
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> updateEventListener = new Listener<>(event -> {
        if(mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle || mc.player.getHeldItemOffhand().getItem() instanceof ItemExpBottle)
            mc.rightClickDelayTimer = 0;
    });
}