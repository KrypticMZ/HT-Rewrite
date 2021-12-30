package me.htrewrite.client.module.modules.gui;

import jaco.mp3.player.MP3Player;
import me.htrewrite.client.clickgui.components.buttons.settings.bettermode.BetterMode;
import me.htrewrite.client.event.custom.client.ClientSettingChangeEvent;
import me.htrewrite.client.event.custom.client.ClientShutdownEvent;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import me.htrewrite.exeterimports.mcapi.settings.ModeSetting;
import me.htrewrite.exeterimports.mcapi.settings.ToggleableSetting;
import me.htrewrite.exeterimports.mcapi.settings.ValueSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

import java.io.File;
import java.util.Arrays;

public class MusicModule extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", null, 0, BetterMode.construct("LIST", "SHUFFLE"));
    public static final ValueSetting<Double> volume = new ValueSetting<>("Volume", null, 80D, 0D, 100D);
    public static final ToggleableSetting looped = new ToggleableSetting("Repeat", null, true);

    MP3Player mp3Player = null;

    public MusicModule() {
        super("Music", "Music player.", ModuleType.Gui, 0);
        addOption(mode);
        addOption(volume);
        addOption(looped);
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        sendMessage("Booting up music system, please wait...");

        File folder = new File("htRewrite\\music\\");
        folder.mkdirs();
        File[] files = Arrays.stream(folder.listFiles()).filter(file -> validFileCheck(file)).toArray(File[]::new);

        if(files.length < 1) {
            sendMessage("&cPlease add &l'.mp3' &csongs into &l'htRewrite\\music\\' &cfolder.");
            toggle();
            return;
        }

        sendMessage("Adding songs to the list...");
        mp3Player = new MP3Player();
        for(File file : files) {
            mp3Player.add(file, true);
            sendMessage("&r   - " + file.getName());
        }
        mp3Player.setRepeat(looped.isEnabled());
        mp3Player.setShuffle(mode.getValue().contentEquals("SHUFFLE"));
        mp3Player.setVolume(volume.getValue().intValue());

        sendMessage("&aPlaying now!");
        mp3Player.play();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        sendMessage("Stopping sound system, please wait...");
        mp3Player.stop();
    }

    @EventHandler
    private Listener<ClientShutdownEvent> shutdownEventListener = new Listener<>(event -> {
        if(isEnabled())
            toggle();
    });

    @EventHandler
    private Listener<ClientSettingChangeEvent> settingChangeEventListener = new Listener<>(event -> {
        if(mp3Player == null)
            return;

        if(event.setting == mode)
            mp3Player.setShuffle(mode.getValue().contentEquals("SHUFFLE"));
        if(event.setting == volume)
            mp3Player.setVolume(volume.getValue().intValue());
        if(event.setting == looped)
            mp3Player.setRepeat(looped.isEnabled());
    });

    public boolean validFileCheck(File file) {
        String fileName = file.toString();
        int index = fileName.lastIndexOf('.');
        return index > 0 && fileName.substring(index+1).equalsIgnoreCase("mp3");
    }
}