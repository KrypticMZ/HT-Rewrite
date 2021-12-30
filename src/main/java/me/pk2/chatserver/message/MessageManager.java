package me.pk2.chatserver.message;

import me.pk2.chatserver.user.User;
import me.pk2.chatserver.user.UserManager;

public class MessageManager {
    public static void queueMessage(Message message) {
        for(User user : UserManager.users)
            if(user.isAlive())
                user.messagesInQueue.add(message);
            else user.messagesInQueue.clear();
    }
}