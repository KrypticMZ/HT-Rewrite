package me.htrewrite.exeterimports.keybind;

import me.htrewrite.exeterimports.mcapi.manager.SetManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeybindManager extends SetManager<Keybind> {
    public KeybindManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInput(InputEvent.KeyInputEvent inputEvent) {
        boolean eventKeyState = Keyboard.getEventKeyState();
        int eventKey = Keyboard.getEventKey();
        if(!eventKeyState) return;

        if (eventKey != Keyboard.KEY_NONE && Keyboard.getEventKeyState()) {
            for (Keybind keybind : getSet()) {
                if (keybind.getKey() == eventKey) {
                    keybind.onPressed();
                    break;
                }
            }
        }
    }
}