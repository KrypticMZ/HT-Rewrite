package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.event.custom.player.PlayerUpdateMoveStateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.client.module.modules.world.AutoTunnelModule;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class AutoWalkModule extends Module {
    public static AutoWalkModule INSTANCE;

    public AutoWalkModule() {
        super("AutoWalk", "It makes you walk.", ModuleType.Movement, 0);
        endOption();

        INSTANCE = this;
    }

    AutoTunnelModule autoTunnelModule;

    @Override
    public void onEnable() {
        super.onEnable();

        if(mc.player == null)
            toggle();
        autoTunnelModule = (AutoTunnelModule) HTRewrite.INSTANCE.getModuleManager().getModuleByClass(AutoTunnelModule.class);
    }

    @EventHandler
    private Listener<PlayerUpdateMoveStateEvent> playerUpdateMoveStateListener = new Listener<>(event -> {
        if(!autoTunnelModule.pauseAutoWalk())
            mc.player.movementInput.moveForward++;
    });
}