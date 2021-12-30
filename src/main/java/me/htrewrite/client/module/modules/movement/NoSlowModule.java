package me.htrewrite.client.module.modules.movement;

import me.htrewrite.client.Wrapper;
import me.htrewrite.client.event.custom.client.ClientKeyEvent;
import me.htrewrite.client.event.custom.networkmanager.NetworkPacketEvent;
import me.htrewrite.client.event.custom.player.PlayerUpdateEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import org.lwjgl.input.Keyboard;

public class NoSlowModule extends Module {
    public static final ToggleableSetting guiMove = new ToggleableSetting("GuiMove", null, true);
    public static final ToggleableSetting noSlow = new ToggleableSetting("NoSlow", null, true);
    public static final ToggleableSetting soulSand = new ToggleableSetting("SoulSand", null, false);
    public static final ToggleableSetting strict = new ToggleableSetting("Strict", null, false);
    public static final ToggleableSetting webs = new ToggleableSetting("Webs", null, false);
    public static final ValueSetting<Double> webHorizontalFactor = new ValueSetting<>("WebHSpeed", null, 2d, 0d, 100d);
    public static final ValueSetting<Double> webVerticalFactor = new ValueSetting<>("WebVSpeed", null, 2d, 0d, 100d);

    private static KeyBinding[] keys = new KeyBinding[]{Wrapper.getMC().gameSettings.keyBindForward, Wrapper.getMC().gameSettings.keyBindBack, Wrapper.getMC().gameSettings.keyBindLeft, Wrapper.getMC().gameSettings.keyBindRight, Wrapper.getMC().gameSettings.keyBindJump, Wrapper.getMC().gameSettings.keyBindSprint};
    private static NoSlowModule instance;

    public NoSlowModule() {
        super("NoSlow", "Move faster on things that slow you down.", ModuleType.Movement, 0);
        addOption(guiMove);
        addOption(noSlow);
        // addOption(soulSand);
        addOption(strict);
        addOption(webs);
        addOption(webHorizontalFactor.setVisibility(v -> webs.isEnabled()));
        addOption(webVerticalFactor.setVisibility(v -> webs.isEnabled()));
        endOption();

        instance = this;
    }

    @EventHandler
    private Listener<PlayerUpdateEvent> playerUpdateEventListener = new Listener<>(event -> {
        if(guiMove.isEnabled()) {
            if(mc.currentScreen instanceof GuiOptions || mc.currentScreen instanceof GuiVideoSettings || mc.currentScreen instanceof GuiScreenOptionsSounds || mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiIngameMenu)
                for (KeyBinding bind : keys)
                    KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
        } else if(mc.currentScreen == null) {
            for (KeyBinding bind : keys) {
                if (Keyboard.isKeyDown(bind.getKeyCode()))
                    continue;

                KeyBinding.setKeyBindState(bind.getKeyCode(), false);
            }
        }

        if(webs.isEnabled() && mc.player.isInWeb) {
            mc.player.motionX *= webHorizontalFactor.getValue().doubleValue();
            mc.player.motionZ *= webHorizontalFactor.getValue().doubleValue();
            mc.player.motionY *= webVerticalFactor.getValue().doubleValue();
        }
    });

    @EventHandler
    private Listener<InputUpdateEvent> inputUpdateEventListener = new Listener<>(event -> {
        if(noSlow.isEnabled() && mc.player.isHandActive() && !mc.player.isRiding()) {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    });

    @EventHandler
    private Listener<ClientKeyEvent> clientKeyEventListener = new Listener<>(event -> {
        if (guiMove.isEnabled() && !(mc.currentScreen instanceof GuiChat))
            event.info = event.pressed;
    });

    @EventHandler
    private Listener<NetworkPacketEvent> packetEventListener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketPlayer && strict.isEnabled() && noSlow.isEnabled() && mc.player.isHandActive() && !mc.player.isRiding())
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
    });

    public static NoSlowModule getInstance() { return instance; }
}