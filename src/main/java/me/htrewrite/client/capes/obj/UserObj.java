package me.htrewrite.client.capes.obj;

public class UserObj {
    public final int id;
    public final String username;
    public final int cape_id;

    public UserObj(int id, String username, int cape_id) {
        this.id = id;
        this.username = username;
        this.cape_id = cape_id;
    }
}