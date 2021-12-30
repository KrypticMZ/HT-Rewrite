package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.module.Module;
import me.htrewrite.client.util.ChatColor;
import me.htrewrite.exeterimports.mcapi.settings.*;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

public class ModuleCommand extends Command {
    public ModuleCommand() {
        super("module", "<module> {['reset']} {[config] [value]} {['toggle']} {['bind'] [key_name]} {'unbind'}", "Useful for setting module config.");
    }

    @Override
    public void call(String[] args) {
        if(args.length < 1) {
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cInvalid syntax! &l" + formatCmd(getAlias() + " " + getUsage()))));
            return;
        }
        Module module = HTRewrite.INSTANCE.getModuleManager().getModuleByLowercaseName(args[0]);
        if(module == null) {
            StringBuilder message = new StringBuilder();
            message.append(ChatColor.prefix_parse('&', "&eAvailable modules:\n"));
            for(Module module1 : HTRewrite.INSTANCE.getModuleManager().getModules())
                message.append(ChatColor.prefix_parse('&', "   &r" + module1.getName() + " [" + Keyboard.getKeyName(module1.getKey()) + "] - &l" + module1.getDesc() + "\n"));
            mc.player.sendMessage(new TextComponentString(message.toString()));
            return;
        }
        if(args.length < 2) {
            StringBuilder message = new StringBuilder();
            message.append(ChatColor.prefix_parse('&', "&eShowing module info:\n"));
            message.append(ChatColor.prefix_parse('&', "   &rNAME: '&l" + module.getName() + "&r'\n"));
            message.append(ChatColor.prefix_parse('&', "   &rKEY: '&l" + Keyboard.getKeyName(module.getKey()) + "&r'\n"));
            message.append(ChatColor.prefix_parse('&', "   &rCONFIG: \n"));
            for(Setting setting : module.getOptions()) {
                if(setting instanceof ModeSetting) {
                    ModeSetting modeSetting = (ModeSetting)setting;
                    message.append(ChatColor.prefix_parse('&', "     - " + modeSetting.getLabel() + "(&l" + modeSetting.getValue() + "&r) &o[" + ChatColor.betterList(modeSetting.modes) + "]"));
                } else if(setting instanceof StringSetting) {
                    StringSetting stringSetting = (StringSetting)setting;
                    message.append(ChatColor.prefix_parse('&', "     - " + stringSetting.getLabel() + "(&l\"" + stringSetting.getValue() + "\"&r)"));
                } else if(setting instanceof ToggleableSetting) {
                    ToggleableSetting toggleableSetting = (ToggleableSetting)setting;
                    message.append(ChatColor.prefix_parse('&', "     - " + toggleableSetting.getLabel() + "(&l" + toggleableSetting.isEnabled() + "&r)"));
                } else if(setting instanceof ValueSetting) {
                    ValueSetting valueSetting = (ValueSetting)setting;
                    message.append(ChatColor.prefix_parse('&', "     - " + valueSetting.getLabel() + "(&l\"" + valueSetting.getValue() + "\"&r) &o[" + valueSetting.getMinimum() + " : " + valueSetting.getMaximum() + "]"));
                }
                message.append("\n");
            }
            mc.player.sendMessage(new TextComponentString(message.toString()));
            return;
        }
        if(args[1].equalsIgnoreCase("toggle")) {
            module.toggle();
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&r&l" + module.getName() + " " + (module.isEnabled()?"&ahas been toggled ON.":"&chas been toggled OFF."))));
            return;
        }
        if(args[1].equalsIgnoreCase("unbind")) {
            module.setKey(0);
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&aUnbinded the key of " + module.getName())));
            return;
        }
        if(args[1].equalsIgnoreCase("reset")) {
            for(Setting setting : module.getOptions())
                if(setting instanceof ModeSetting)
                    ((ModeSetting) setting).setValue(((ModeSetting) setting).defaultIndex);
                else if(setting instanceof ToggleableSetting)
                    ((ToggleableSetting) setting).setEnabled(((ToggleableSetting) setting).defaultValue);
                else if(setting instanceof ValueSetting)
                    ((ValueSetting<?>) setting).setValue(((ValueSetting<?>) setting).defaultValue);
                else if(setting instanceof StringSetting)
                    ((StringSetting) setting).setValue(((StringSetting) setting).getValue());

            sendMessage("&cChanged all config to default!");
            return;
        }
        if(args.length == 3) {
            if(args[1].equalsIgnoreCase("bind")) {
                int key = Keyboard.getKeyIndex(args[2]);
                if(key == 0) {
                    mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cInvalid key!")));
                    return;
                }
                module.setKey(key);
                mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&aSet the key of " + module.getName() + " to '&l" + args[2] + "&r'")));
                return;
            }
            Setting setting = module.getOptionByLowercaseName(args[1]);
            if(setting == null) {
                mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cOption doesn't exists.")));
                return;
            }
            if(setting instanceof ModeSetting) {
                ModeSetting modeSetting = (ModeSetting)setting;
                int e = modeSetting.getModeIndexByName(args[2]);
                if(e == -1) {
                    mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cInvalid value.")));
                    return;
                }
                modeSetting.setValue(e);
            } else if(setting instanceof StringSetting) {
                StringSetting stringSetting = (StringSetting)setting;
                stringSetting.setValue(args[2]);
            } else if(setting instanceof ToggleableSetting) {
                ToggleableSetting toggleableSetting = (ToggleableSetting)setting;
                toggleableSetting.setEnabled(Boolean.parseBoolean(args[2]));
            } else if(setting instanceof ValueSetting) {
                ValueSetting valueSetting = (ValueSetting)setting;
                valueSetting.setValue(Double.parseDouble(args[2]));
            }
            mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&aOption changed!")));
            return;
        }
        mc.player.sendMessage(new TextComponentString(ChatColor.prefix_parse('&', "&cInvalid syntax! &l" + formatCmd(getAlias() + " " + getUsage()))));
    }
}