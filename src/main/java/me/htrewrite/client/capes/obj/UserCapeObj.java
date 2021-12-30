package me.htrewrite.client.capes.obj;

public class UserCapeObj {
    public final int id;
    public final String player;
    public final int user_id, cape_id;

    public UserCapeObj(int id, String player, int user_id, int cape_id) {
        this.id = id;
        this.player = player;
        this.user_id = user_id;
        this.cape_id = cape_id;
    }
}