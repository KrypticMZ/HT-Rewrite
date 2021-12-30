package me.pk2.chatserver.clientside.objects;

import me.pk2.chatserver.message.Message;
import me.pk2.chatserver.user.User;

import java.util.List;

public class KeepAliveResponse {
    public final List<User> users;
    public final List<Message> queued_messages;
    public KeepAliveResponse(List<User> users, List<Message> queued_messages) {
        this.users = users;
        this.queued_messages = queued_messages;
    }
}