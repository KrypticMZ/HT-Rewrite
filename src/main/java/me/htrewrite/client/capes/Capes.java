package me.htrewrite.client.capes;

import me.htrewrite.client.capes.obj.CapeObj;
import me.htrewrite.client.capes.obj.UserCapeObj;
import me.htrewrite.client.capes.obj.UserObj;
import me.htrewrite.client.util.AuthSession;
import me.htrewrite.client.util.Entry;
import me.htrewrite.client.util.PostRequest;
import net.minecraft.client.resources.FolderResourcePack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Capes {
    private HashMap<Integer, CapeObj> capes;
    private HashMap<Integer, UserObj> users;
    private ArrayList<UserCapeObj> user_capes;
    private UserObj user;

    public HashMap<Entry<UserObj, String>, CapeObj> wCapes;
    public ArrayList<Entry<String, CapeObj>> myCapes;

    public String debug_name = "";
    public String debug_fileName = "";
    public String debug_url = "";
    public int debug_id = 0;

    public void refresh() {
        this.capes = new HashMap<>();

        String capesResponse = PostRequest.urlEncodedPostRequest("https://aurahardware.eu/api/cape/list_capes.php", "");
        String[] capesSplit = capesResponse.split("]");
        for(String line : capesSplit) {
            String[] row = line.split(";");
            capes.put(Integer.parseInt(row[0]), new CapeObj(Integer.parseInt(row[0]), row[1], row[2]));
        }

        this.users = new HashMap<>();

        String usersResponse = PostRequest.urlEncodedPostRequest("https://aurahardware.eu/api/user/get_user_ids.php", "");
        if(!usersResponse.contentEquals("")) {
            String[] userLines = usersResponse.split("]");
            for (String line : userLines) {
                String[] row = line.split(";");
                users.put(Integer.parseInt(row[0]), new UserObj(Integer.parseInt(row[0]), row[1], Integer.parseInt(row[2])));
            }
        }

        for(UserObj userObj : users.values())
            if(userObj.username.contentEquals(AuthSession.USERNAME))
                user=userObj;

        this.user_capes = new ArrayList<>();

        String userCapesResponse = PostRequest.urlEncodedPostRequest("https://aurahardware.eu/api/cape/user_capes.php", "");
        if(!userCapesResponse.contentEquals("")) {
            String[] user_capes_split;
            if(userCapesResponse.contains("]"))
                user_capes_split = userCapesResponse.split("]");
            else user_capes_split = new String[]{userCapesResponse};

            for(String line : user_capes_split) {
                String[] row = line.split(";");
                user_capes.add(new UserCapeObj(Integer.parseInt(row[0]), row[1], Integer.parseInt(row[2]), Integer.parseInt(row[3])));
            }
        }

        this.wCapes = new HashMap<>();
        this.myCapes = new ArrayList<>();
        for(UserCapeObj userCapeObj : user_capes) {
            UserObj userObj = users.get(userCapeObj.user_id);
            if(userObj.username.contentEquals(AuthSession.USERNAME))
                myCapes.add(new Entry<>(userCapeObj.player, capes.get(userCapeObj.cape_id)));
            if(userCapeObj.id == userObj.cape_id)
                wCapes.put(new Entry<>(userObj, userCapeObj.player), capes.get(userObj.id));
        }
    }

    public CapeObj getWCapeByUsername(String username) {
        for(Map.Entry<Entry<UserObj, String>, CapeObj> entry : wCapes.entrySet())
            if(entry.getKey().getValue().contentEquals(username) && entry.getValue().id==user.cape_id)
                return entry.getValue();
        return null;
    }

    public Capes() {
        refresh();
    }
}