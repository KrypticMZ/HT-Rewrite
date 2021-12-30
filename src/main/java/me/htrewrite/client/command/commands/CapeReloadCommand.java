package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;

import java.util.concurrent.atomic.AtomicBoolean;

public class CapeReloadCommand extends Command {
    public static final AtomicBoolean reloading = new AtomicBoolean(false);

    public CapeReloadCommand() {
        super("cape-reload", "", "Reloads the local database of capes.");
    }

    @Override
    public void call(String[] args) {
        HTRewrite.apiCallsQueue.submit(() -> {
            reloading.set(true);
            sendMessage("&eReloading database...");
            HTRewrite.INSTANCE.getCapes().refresh();
            sendMessage("&aDone reloading database!");
            reloading.set(false);
        });
    }
}