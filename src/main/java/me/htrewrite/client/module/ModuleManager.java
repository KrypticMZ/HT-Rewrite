package me.htrewrite.client.module;

import me.htrewrite.client.module.modules.combat.*;
import me.htrewrite.client.module.modules.exploit.*;
import me.htrewrite.client.module.modules.gui.*;
import me.htrewrite.client.module.modules.gui.hud.HUDModule;
import me.htrewrite.client.module.modules.miscellaneous.*;
import me.htrewrite.client.module.modules.movement.*;
import me.htrewrite.client.module.modules.render.*;
import me.htrewrite.client.module.modules.world.*;

import java.util.ArrayList;

public class ModuleManager {
    private ArrayList<Module> modules;

    public ModuleManager() {
        this.modules = new ArrayList<Module>();
    }

    public static NotificationsModule notificationsModule;
    public void setModules() {
        /* Combat */
        modules.add(new AutoAimModule());
        modules.add(new AutoCityModule());
        modules.add(new AutoTotemModule());
        // modules.add(new AutoSelfShutdown());
        modules.add(new BowBombModule());
        modules.add(new BowSpamModule());
        modules.add(new FastXPModule());
        modules.add(new InstantBurrowModule());
        modules.add(new KillAuraModule());
        modules.add(new VelocityModule());
        /* Exploits */
        modules.add(new AntiChunkBanModule());
        modules.add(new AntiHungerModule());
        modules.add(new AntiMapBanModule());
        // modules.add(new AutoSelfCrashModule());
        modules.add(new BuildHeightModule());
        modules.add(new CoordExploit2Module());
        modules.add(new CoordExploit3Module());
        modules.add(new CoordExploitModule());
        modules.add(new HandshakeSpoofModule());
        modules.add(new KnockbackPlusModule());
        modules.add(new NewChunksModule());
        modules.add(new PacketCancellerModule());
        modules.add(new ReachModule());
        modules.add(new TickShiftMudule());
        /* Miscellaneous */
        modules.add(new AntiLog4jExploitModule());
        modules.add(new AutoDupeModule());
        modules.add(new AutoEatModule());
        modules.add(new AutoFrameDupeModule());
        // modules.add(new AutoGearModule());
        modules.add(new AutoRacismModule());
        modules.add(new AutoReplyModule());
        modules.add(new ChatModule());
        modules.add(new FakePlayerModule());
        modules.add(new JesusModule()); // TODO: Fix module.
        modules.add(new MiddleClickModule());
        modules.add(new MobOwnerModule());
        modules.add(new NoFallModule());
        modules.add(new SignPlusModule());
        modules.add(new SpammerModule());
        modules.add(new TabExpanderModule());
        modules.add(new TimestampsModule());
        modules.add(new XCarryModule());
        /* Movement */
        // modules.add(new AnchorModule());
        modules.add(new AntiLevitateModule());
        modules.add(new AutoWalkModule());
        modules.add(new BlinkModule());
        modules.add(new BoatFlyModule());
        modules.add(new ElytraFly2Module());
        modules.add(new ElytraFlyModule());
        modules.add(new EntityControlModule());
        modules.add(new EntitySpeedModule());
        modules.add(new FreecamModule());
        modules.add(new LongJumpModule());
        modules.add(new NoSlowModule());
        modules.add(new ReverseStepModule());
        modules.add(new SprintModule());
        modules.add(new StepModule());
        /* Render */
        modules.add(new AntiEnchantmentTableLagModule());
        modules.add(new ChamsModule());
        modules.add(new ChestESPModule());
        modules.add(new CityESPModule());
        modules.add(new FullbrightModule());
        modules.add(new HandProgressModule());
        // modules.add(new HoleESPModule()); // TODO: Fix
        modules.add(new IbaiModule());
        // modules.add(new NametagsModule());
        modules.add(new NoFogModule());
        modules.add(new NoRenderModule());
        modules.add(new ShulkerPreviewModule());
        modules.add(new SkyColorModule());
        modules.add(new SlowSwingModule());
        modules.add(new TracersModule());
        // modules.add(new XRayModule());
        /* World */
        modules.add(new AutoTunnelModule());
        modules.add(new EntityLoggerModule());
        modules.add(new FastPlaceModule());
        modules.add(new LawnmowerModule());
        modules.add(new NoRainModule());
        modules.add(new PhaseModule());
        modules.add(new PortalModule());
        // modules.add(new ScaffoldModule()); /* TODO: Fix Scaffold */
        modules.add(new SpeedmineModule());
        modules.add(new StashLoggerModule());
        /* Gui */
        modules.add(new ClickGuiModule());
        modules.add(new DiscordRPCModule());
        modules.add(new HUDModule());
        modules.add(new MusicModule());
        modules.add(notificationsModule = new NotificationsModule());
    }

    public ArrayList<Module> getModules() {
        return modules;
    }
    public ArrayList<Module> getModulesByCategory(ModuleType moduleType) {
        ArrayList<Module> modules = new ArrayList<Module>();
        for(Module module : getModules())
            if(module.getCategory() == moduleType)
                modules.add(module);
        return modules;
    }
    public Module getModuleByClass(Class<? extends Module> moduleClass) {
        for(Module module : getModules())
            if(module.getClass() == moduleClass)
                return module;
        return null;
    }
    public Module getModuleByLowercaseName(String moduleName) {
        for(Module module : getModules())
            if(module.getName().equalsIgnoreCase(moduleName))
                return module;
        return null;
    }
}
