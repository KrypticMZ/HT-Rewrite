package me.htrewrite.client.waypoints;

import java.util.ArrayList;

public class Waypoints {
    private ArrayList<Waypoint> waypoints;

    public Waypoints() {
        this.waypoints = new ArrayList<>();
    }

    public Waypoint get(String name) {
        for(Waypoint waypoint : waypoints)
            if(waypoint.name.contentEquals(name))
                return waypoint;
        return null;
    }
    public boolean add(Waypoint waypoint) {
        if(get(waypoint.name) != null)
            return false;

        waypoints.add(waypoint);
        return true;
    }
    public boolean remove(String name) {
        for(int i = 0; i < waypoints.size(); i++)
            if(waypoints.get(i).name.contentEquals(name)) {
                waypoints.remove(i);
                return true;
            } return false;
    }

    public Waypoint[] getWaypoints() { return waypoints.toArray(new Waypoint[0]); }
}