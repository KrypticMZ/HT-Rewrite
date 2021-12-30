package me.htrewrite.client.module.modules.miscellaneous;

import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.util.TickedTimer;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Comparator;

public class AutoFrameDupeModule extends Module {
    public static final ValueSetting<Double> tickDelay = new ValueSetting<>("TickDelay", null, 10d, 0d, 20d);

    private TickedTimer tickedTimer;
    private boolean sending = false;
    private Entity entity;
    public AutoFrameDupeModule() {
        super("AutoFrameDupe", "Performs the frame dupe.", ModuleType.Miscellaneous, 0);
        addOption(tickDelay);
        endOption();

        tickedTimer = new TickedTimer();
        tickedTimer.stop();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        sending = false;
        tickedTimer.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        tickedTimer.stop();
    }

    private boolean isValidTileEntity(Entity entity) { return (entity instanceof EntityItemFrame)&&mc.player.getDistance(entity)<4f; }

    @EventHandler
    private Listener<TickEvent.ClientTickEvent> updateEventListener = new Listener<>(event -> {
        if(nullCheck() || !tickedTimer.passed(tickDelay.getValue().intValue()))
            return;
        entity = mc.world.loadedEntityList.stream()
                .filter(loadedEntity -> isValidTileEntity(loadedEntity))
                .min(Comparator.comparing(loadedEntity -> mc.player.getDistance(loadedEntity.getPosition().getX(), loadedEntity.getPosition().getY(), loadedEntity.getPosition().getZ())))
                .orElse(null);
        EntityItemFrame itemFrame = (EntityItemFrame)entity;
        if(entity == null) {
            sendMessage("&cItemFrame not found in a range of 4 blocks!");
            toggle();
            return;
        }

        if(mc.player.getHeldItemMainhand() == null || mc.player.getHeldItemMainhand().getItem() == Items.AIR)
            return;

        if(sending && (itemFrame.getDisplayedItem() == null || itemFrame.getDisplayedItem().getItem()==Items.AIR))
            sending = false;
        mc.player.connection.sendPacket(sending?new CPacketUseEntity(entity):new CPacketUseEntity(entity, EnumHand.MAIN_HAND));
        sending = !sending;
        tickedTimer.reset();
    });
}