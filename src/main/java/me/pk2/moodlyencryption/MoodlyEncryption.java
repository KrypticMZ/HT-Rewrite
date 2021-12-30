package me.pk2.moodlyencryption;

import me.pk2.moodlyencryption.util.RandomString;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;

public class MoodlyEncryption {
    private String key;
    private Key aesKey;
    private Cipher cipher;

    public MoodlyEncryption() throws Exception {
        init(new RandomString(16).nextString());
    }

    public void init(String key) throws Exception {
        this.key = key;
        aesKey = new SecretKeySpec(key.getBytes(), String.valueOf((char)0b1000001) + String.valueOf((char)0105) + String.valueOf((char)0123));
        cipher = Cipher.getInstance(String.valueOf((char)0b1000001) + String.valueOf((char)0105) + String.valueOf((char)0123));
    }

    public byte[] encrypt(String in) throws Exception {
        char[] inArr = in.toCharArray();
        ArrayList<String> outArr = new ArrayList<>();
        for(char c : inArr)
            outArr.add(new String(Base64.getEncoder().encode(String.valueOf(c).getBytes())));
        StringBuilder outB = new StringBuilder();
        for(String o : outArr)
            outB.append((o==outArr.get(outArr.size()-1))?o:o+String.valueOf((char)45));
        byte[] out = Base64.getEncoder().encode(outB.toString().getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        out = cipher.doFinal(out);
        out = Base64.getEncoder().encode(out);

        outArr.clear();
        return out;
    }

    public String decrypt(byte[] in) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        in = Base64.getDecoder().decode(in);
        String out = new String(cipher.doFinal(in));
        out = new String(Base64.getDecoder().decode(out.getBytes()));
        String[] inArr = out.split(String.valueOf((char)45));
        StringBuilder outB = new StringBuilder();
        for(String s : inArr)
            outB.append(new String(Base64.getDecoder().decode(s)));
        out = outB.toString();

        return out;
    }

    public String getKey() { return key; }
}