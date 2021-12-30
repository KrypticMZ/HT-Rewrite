package me.htrewrite.client.module.modules.render;

import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.PlayerIsPotionActiveEvent;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.event.custom.render.*;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.Timer;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

import java.util.Iterator;

public class NoRenderModule extends Module {
    public static ToggleableSetting noitems = new ToggleableSetting("NoItems", false);
    public static ToggleableSetting fire = new ToggleableSetting("Fire", false);
    public static ToggleableSetting hurtcamera = new ToggleableSetting("NoHurtCamera", false);
    public static ToggleableSetting nopumpkin = new ToggleableSetting("NoPumpkin", false);
    public static ToggleableSetting blindness = new ToggleableSetting("Blindness", false);
    public static ToggleableSetting totemAnimation = new ToggleableSetting("TotemAnimation", false);
    public static ToggleableSetting signText = new ToggleableSetting("SignText", false);
    public static ToggleableSetting NoArmor = new ToggleableSetting("NoArmor", false);
    public static ToggleableSetting NoArmorPlayers = new ToggleableSetting("NoArmorPlayers", true);
    public static ToggleableSetting maps = new ToggleableSetting("Maps", false);
    public static ToggleableSetting bossbars = new ToggleableSetting("BossBars", false);

    public NoRenderModule() {
        super("NoRender", "Deletes some hud stuff", ModuleType.Render, 0);
        addOption(noitems);
        addOption(fire);
        addOption(hurtcamera);
        addOption(nopumpkin);
        addOption(blindness);
        addOption(totemAnimation);
        addOption(signText);
        addOption(NoArmor);
        addOption(NoArmorPlayers);
        addOption(maps);
        addOption(bossbars);
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();

    }

    private Timer timer = new Timer();


    @EventHandler
    private Listener<PlayerUpdateEvent> onPlayerUpdate = new Listener<>(event ->
    {
        if (noitems.isEnabled()) {
            if (!timer.passed(5000))
                return;

            timer.reset();

            Iterator<Entity> itr = mc.world.loadedEntityList.iterator();

            while (itr.hasNext())
            {
                net.minecraft.entity.Entity entity = itr.next();

                if (entity != null)
                {
                    if (entity instanceof EntityItem)
                        mc.world.removeEntity(entity);
                }
            }
        }
    });
    @EventHandler
    private Listener<RenderHurtCameraEvent> OnHurtCameraEffect = new Listener<>(p_Event ->
    {
        if (hurtcamera.isEnabled())
            p_Event.cancel();
    });
    @EventHandler
    private Listener<RenderBlockOverlayEvent> OnBlockOverlayEvent = new Listener<>(p_Event ->
    {
        if (fire.isEnabled() && p_Event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE)
            p_Event.setCanceled(true);
        if (nopumpkin.isEnabled() && p_Event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK)
            p_Event.setCanceled(true);
    });
    @EventHandler
    private Listener<PlayerIsPotionActiveEvent> IsPotionActive = new Listener<>(p_Event ->
    {
        if (p_Event.potion == MobEffects.BLINDNESS && blindness.isEnabled())
            p_Event.cancel();
    });
    @EventHandler
    private Listener<NetworkPacketEvent> PacketEvent = new Listener<>(p_Event ->
    {
        if (mc.world == null || mc.player == null)
            return;

        if (p_Event.getPacket() instanceof SPacketEntityStatus)
        {
            SPacketEntityStatus l_Packet = (SPacketEntityStatus)p_Event.getPacket();

            if (l_Packet.getOpCode() == 35)
            {
                if (totemAnimation.isEnabled())
                    p_Event.cancel();
            }
        }
    });
    @EventHandler
    private Listener<RenderSignEvent> OnRenderSign = new Listener<>(p_Event ->
    {
        if (signText.isEnabled())
            p_Event.cancel();
    });
    @EventHandler
    private Listener<RenderArmorLayerEvent> OnRenderArmorLayer = new Listener<>(p_Event ->
    {
        if (NoArmor.isEnabled())
        {
            if (!(p_Event.entity instanceof EntityPlayer) && NoArmorPlayers.isEnabled())
                return;

            p_Event.cancel();
        }
    });
    @EventHandler
    private Listener<RenderMapEvent> OnRenderMap = new Listener<>(p_Event ->
    {
        if (maps.isEnabled())
            p_Event.cancel();
    });
    @EventHandler
    private Listener<RenderBossHealthEvent> OnRenderBossHealth = new Listener<>(p_Event ->
    {
        if (bossbars.isEnabled())
            p_Event.cancel();
    });
}

