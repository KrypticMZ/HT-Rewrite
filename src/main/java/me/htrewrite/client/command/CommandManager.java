package me.htrewrite.client.command;

import static me.htrewrite.client.command.CommandReturnStatus.*;

import me.htrewrite.client.command.commands.*;
import me.htrewrite.client.module.ModuleManager;
import me.htrewrite.client.module.modules.gui.NotificationsModule;
import me.htrewrite.client.util.ConfigUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public class CommandManager {
    public static String prefix = "'";

    public final ConfigUtils configUtils;
    private ArrayList<Command> commands;

    private HTChatCommand chatCommand;
    public CommandManager() {
        configUtils = new ConfigUtils("config", "commands");
        Object object = configUtils.get("prefix");
        if(object == null) configUtils.set("prefix", prefix);
        else prefix = (String)object;

        commands = new ArrayList<Command>();
        /*commands.add(new CapeClaimCommand());
        commands.add(new CapeDebugCommand());
        commands.add(new CapeListCommand());    DISABLED CAPES UNTIL FIX
        commands.add(new CapeReloadCommand());
        commands.add(new CapeUseCommand());*/
        commands.add(new ChatExceptCommand());
        commands.add(new ClipCommand());
        commands.add(new CoordExploitCommand());
        commands.add(new FriendCommand());
        commands.add(new HelpCommand());
        commands.add(chatCommand = new HTChatCommand());
        commands.add(new HTListCommand());
        commands.add(new ModuleCommand());
        commands.add(new PrefixCommand(this));
        commands.add(new TestSoundCommand());
        commands.add(new VanishCommand());
        // commands.add(new XRayCommand());
    }

    public Command get(String alias) {
        for(Command command : commands)
            if(command.getAlias().equalsIgnoreCase(alias))
                return command;
        return null;
    }
    public CommandReturnStatus gotMessage(String message) {
        if(message.startsWith("%") && NotificationsModule.receiveChatNotifications.isEnabled() && ModuleManager.notificationsModule.isEnabled()) {
            chatCommand.call(message.substring(1).split(" "));
            return COMMAND_HTCHAT;
        }
        if(!message.startsWith(prefix)) return COMMAND_INVALID_SYNTAX;
        String[] args = message.split(" ");
        args[0] = args[0].replaceFirst(prefix, "");
        Command command = get(args[0]);
        if(command == null) return COMMAND_INVALID;
        args = ArrayUtils.remove(args, 0);
        command.call(args);
        return COMMAND_VALID;
    }

    public ConfigUtils getConfigUtils() { return configUtils; }
    public ArrayList<Command> getCommands() { return commands; }

    public void setPrefix(String newPrefix) {
        CommandManager.prefix = newPrefix;
        configUtils.set("prefix", newPrefix);
    }
}