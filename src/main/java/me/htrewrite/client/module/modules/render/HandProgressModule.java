package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.event.custom.render.RenderUpdateEquippedItemEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.htrewrite.oyveyimports.util.EntityUtil;
import me.htrewrite.salimports.util.PlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.util.EnumHand;


public class HandProgressModule extends Module
{
    public final ValueSetting<Double> MainProgress = new ValueSetting<>("MainProgress", new String[] {""},  0.5d, 0.0d, 1.0d);
    public final ValueSetting<Double> OffProgress = new ValueSetting<>("OffProgress", new String[] {""},  0.5d, 0.0d, 1.0d);

    public HandProgressModule()
    {
        super("HandProgress",null,ModuleType.Render,0);
        addOption(MainProgress);
        addOption(OffProgress);
        endOption();
    }
    @EventHandler
    private Listener<PlayerUpdateEvent> OnPlayerUpdate = new Listener<>(p_Event ->
    {
        mc.entityRenderer.itemRenderer.equippedProgressMainHand = MainProgress.getValue().floatValue();
        mc.entityRenderer.itemRenderer.equippedProgressOffHand = OffProgress.getValue().floatValue();
    });

    @EventHandler
    private Listener<RenderUpdateEquippedItemEvent> OnUpdateEquippedItem = new Listener<>(p_Event ->
    {
        // p_Event.cancel();
        PlayerUtil.mc.entityRenderer.itemRenderer.itemStackMainHand = PlayerUtil.mc.player.getHeldItem(EnumHand.MAIN_HAND);
        PlayerUtil.mc.entityRenderer.itemRenderer.itemStackOffHand = EntityUtil.mc.player.getHeldItem(EnumHand.OFF_HAND);
    });
}