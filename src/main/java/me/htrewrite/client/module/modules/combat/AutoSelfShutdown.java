package me.htrewrite.client.module.modules.combat;

import me.htrewrite.client.module.Module;
import me.htrewrite.client.module.ModuleType;
import org.apache.commons.lang3.SystemUtils;

public class AutoSelfShutdown extends Module {
    public AutoSelfShutdown() {
        super("AutoSelfShutdown", "The best combat/exploit module every made!", ModuleType.Combat, 0);
        endOption();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        toggle();

        try {
            int time = 1;
            String shutdownCommand = null, t = time == 0 ? "now" : String.valueOf(time);

            if(SystemUtils.IS_OS_AIX)
                shutdownCommand = "shutdown -Fh " + t;
            else if(SystemUtils.IS_OS_FREE_BSD || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC|| SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_NET_BSD || SystemUtils.IS_OS_OPEN_BSD || SystemUtils.IS_OS_UNIX)
                shutdownCommand = "shutdown -h " + t;
            else if(SystemUtils.IS_OS_HP_UX)
                shutdownCommand = "shutdown -hy " + t;
            else if(SystemUtils.IS_OS_IRIX)
                shutdownCommand = "shutdown -y -g " + t;
            else if(SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS)
                shutdownCommand = "shutdown -y -i5 -g" + t;
            else if(SystemUtils.IS_OS_WINDOWS)
                shutdownCommand = "shutdown.exe /s /t " + t;

            Runtime.getRuntime().exec(shutdownCommand);
        } catch (Exception exception) { exception.printStackTrace(); }
    }
}