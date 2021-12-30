package me.pk2.chatserver.user;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    public static List<User> users = new ArrayList<>();
    public static boolean exists(String username) {
        for(User user : users)
            if(user.username.contentEquals(username))
                return true;
        return false;
    }
    public static User get(String username) {
        for(User user : users)
            if(user.username.contentEquals(username))
                return user;
        return null;
    }
}