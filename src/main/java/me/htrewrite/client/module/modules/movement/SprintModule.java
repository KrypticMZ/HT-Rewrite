package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.CustomEvent;
import me.htrewrite.client.event.custom.player.PlayerMotionUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class SprintModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", null, 0, BetterMode.construct("LEGIT", "RAGE"));

    @Override public String getMeta() { return mode.getValue(); }
    public SprintModule() {
        super("Sprint", "Sprints for you.", ModuleType.Movement, 0);
        addOption(mode);
        endOption();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(mc.world != null)
            mc.player.setSprinting(false);
    }

    @EventHandler
    private Listener<PlayerMotionUpdateEvent> motionUpdateEventListener = new Listener<>(event -> {
        if(event.getEra() != CustomEvent.Era.PRE)
            return;
        switch (mode.getI()) {
            case 0: {
                if ((mc.gameSettings.keyBindForward.isKeyDown()) && !(mc.player.isSneaking()) && !(mc.player.isHandActive()) && !(mc.player.collidedHorizontally) && mc.currentScreen == null
                        && !(mc.player.getFoodStats().getFoodLevel() <= 6f))
                    mc.player.setSprinting(true);
                break;
            }

            case 1: {
                if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown())
                        && !(mc.player.isSneaking()) && !(mc.player.collidedHorizontally) && !(mc.player.getFoodStats().getFoodLevel() <= 6f))
                    mc.player.setSprinting(true);
                break;
            }

            default:break;
        }
    });
}