package me.htrewrite.client.waypoints;

import org.json.JSONObject;

public class Waypoint {
    public String server, name;
    public int world, x, y, z;
    public Waypoint(String server, String name, int world, int x, int y, int z) {
        this.server = server;
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Waypoint(JSONObject jsonObject) { this(jsonObject.getString("server"), jsonObject.getString("name"), jsonObject.getInt("world"), jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getInt("z")); }

    public String getDimension() { return world==-1?"NETHER":(world==0?"Overworld":(world==1?"End":String.valueOf(world))); }
}