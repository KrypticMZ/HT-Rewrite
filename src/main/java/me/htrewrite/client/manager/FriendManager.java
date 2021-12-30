package me.htrewrite.client.manager;

import me.htrewrite.client.util.ConfigUtils;
import org.json.JSONArray;

import java.util.List;

public class FriendManager {
    public final ConfigUtils configUtils;
    private JSONArray friends;

    public FriendManager() {
        friends = new JSONArray();
        friends.put("pk2_stimpy");
        friends.put("therealcg3");

        configUtils = new ConfigUtils("friends", "");
        if(configUtils.get("friends") == null)
            configUtils.getJSON().put("friends", friends);
        else friends = configUtils.getJSON().getJSONArray("friends");
    }

    public void addFriend(String user) {
        if(!friends.toList().contains(user.toLowerCase()))
            friends.put(user.toLowerCase());
    }
    public void remFriend(String user) {
        if (friends.toList().contains(user.toLowerCase()))
            friends.remove(friends.toList().indexOf(user.toLowerCase()));
    }
    public List<Object> getFriends() { return friends.toList(); }
    public boolean isFriend(String user) { return getFriends().contains(user.toLowerCase()); }
}