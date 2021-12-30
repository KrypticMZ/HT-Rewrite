package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTMinecraft;
import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.util.ConfigUtils;
import me.htrewrite.client.waypoints.Waypoint;
import me.htrewrite.client.waypoints.Waypoints;
import org.json.JSONArray;
import org.json.JSONObject;

public class WaypointsCommand extends Command {
    private ConfigUtils config;
    private Waypoints waypoints;

    public WaypointsCommand() {
        super("waypoints", "{['add'/'remove'] [waypoint]}", "Waypoints management.");
        this.waypoints = HTRewrite.INSTANCE.getWaypoints();

        this.config = new ConfigUtils("waypoints", "waypoints");
        if(config.get("waypoints") == null)
            config.set("waypoints", new JSONArray());
        this.config.save();

        JSONArray array = (JSONArray)config.get("waypoints");
        for(Object object : array)
            this.waypoints.add(new Waypoint((JSONObject)object));
    }

    @Override
    public void call(String[] args) {
        if(args.length == 0) {
            JSONArray waypointsArray = (JSONArray)config.get("waypoints");
            Waypoint[] waypoints = this.waypoints.getWaypoints();
            int count = 0;
            for(int i = 0; i < waypoints.length; i++) {
                Waypoint waypoint = waypoints[i];

                if(count == 0)
                    sendMessage("&eWaypoints for current server:");
                sendMessage("  &a" + waypoint.name + " &e[" + waypoint.x + ", " + waypoint.y + ", " + waypoint.z + "[(" + waypoint.world + ")");
                count++;
            }

            if(count == 0)
                sendMessage("&cNo waypoints found for this server.");
            return;
        }

        if(args.length == 2) {
            switch(args[0].toLowerCase()) {
                case "add": {
                    if(this.waypoints.get(args[1]) == null) {
                        this.waypoints.add(new Waypoint(HTMinecraft.getServer(), args[1], mc.world.provider.getDimension(), (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ));
                        sendMessage("&aAdded waypoint! &e'" + args[1] + "'");
                        break;
                    }

                    Waypoint waypoint = this.waypoints.get(args[1]);
                    waypoint.name = args[1];
                    waypoint.server = HTMinecraft.getServer();
                    waypoint.world = mc.world.provider.getDimension();
                    waypoint.x = (int)mc.player.posX;
                    waypoint.y = (int)mc.player.posY;
                    waypoint.z = (int)mc.player.posZ;
                    sendMessage("&aModified waypoint! &e'" + args[1] + "'");
                } break;

                case "remove":
                case "rem": {
                    sendMessage(this.waypoints.remove(args[1])?"&eRemoved waypoint!" : ("&cWaypoint &e'" + args[1] + "'&c does not exist!"));
                } break;

                default:
                    sendInvalidUsageMessage();
                    break;
            }
        } else sendInvalidUsageMessage();
    }
}