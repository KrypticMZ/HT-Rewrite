package me.htrewrite.client.util;

import org.apache.commons.lang3.StringEscapeUtils;

import java.security.MessageDigest;

public class AuthSession {
    public static String USERNAME, PASSWORD, HWID;
    public static String I_USERNAME, I_PASSWORD, I_HWID;

    public static void entry() {
        ConfigUtils configUtils = new ConfigUtils("auth", "");
        USERNAME = (String)configUtils.get("u");
        PASSWORD = (String)configUtils.get("p");
        HWID = "NOHWID";
        try {
            String fullHWID = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(fullHWID.getBytes());

            StringBuffer buffer = new StringBuffer();

            byte[] md5Bytes = md5.digest();
            for (byte md5Byte : md5Bytes) {
                String hex = Integer.toHexString(0xff & md5Byte);
                buffer.append(hex.length() == 1 ? '0' : hex);
            }
            HWID = "HWID!!" + buffer.toString();
        } catch (Exception exception) {}

        I_USERNAME = StringEscapeUtils.escapeHtml4(USERNAME);
        I_PASSWORD = StringEscapeUtils.escapeHtml4(PASSWORD);
        I_HWID = StringEscapeUtils.escapeHtml4(HWID);
    }
}