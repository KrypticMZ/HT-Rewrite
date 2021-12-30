package me.htrewrite.client.command.commands;

import me.htrewrite.client.command.Command;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class TestSoundCommand extends Command {
    public TestSoundCommand() {
        super("test-sound", "<sound> <volume> <pitch>", "Test minecraft sounds.");
    }

    private SoundEvent getSoundEvent(String name) { return SoundEvent.REGISTRY.getObject(new ResourceLocation(name)); }

    @Override
    public void call(String[] args) {
        if(args.length < 3) {
            sendInvalidUsageMessage();
            return;
        }

        mc.player.playSound(getSoundEvent(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]));
    }
}