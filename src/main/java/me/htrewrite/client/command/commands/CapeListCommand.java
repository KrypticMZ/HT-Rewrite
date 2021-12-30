package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.capes.obj.CapeObj;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.util.Entry;

import java.util.ArrayList;

public class CapeListCommand extends Command {
    public CapeListCommand() {
        super("cape-list", "", "Shows a list of capes you own.");
    }

    @Override
    public void call(String[] args) {
        ArrayList<Entry<String, CapeObj>> myCapes = HTRewrite.INSTANCE.getCapes().myCapes;

        sendMessage("&eAvailable capes: ");
        for(Entry<String, CapeObj> entry : myCapes)
            sendMessage("&b  - [ID="+entry.getValue().id+", Name=" + entry.getValue().name + "] PLAYER=" + entry.getKey());
    }
}