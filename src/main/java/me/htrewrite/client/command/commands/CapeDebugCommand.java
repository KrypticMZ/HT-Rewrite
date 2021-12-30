package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.capes.Capes;
import me.htrewrite.client.command.Command;

public class CapeDebugCommand extends Command {
    public CapeDebugCommand() {
        super("cape-debug", "", "Cape info for debugging purposes.");
    }

    @Override
    public void call(String[] args) {
        Capes capes = HTRewrite.INSTANCE.getCapes();
        sendMessage("["+capes.debug_id+",'"+capes.debug_url+"','"+capes.debug_fileName+"','"+capes.debug_name+"']");
    }
}