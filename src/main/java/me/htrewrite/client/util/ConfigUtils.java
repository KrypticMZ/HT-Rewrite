package me.htrewrite.client.util;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;

public class ConfigUtils {
    private String name;
    private String filePath;
    private String complexFilePath;
    private File file;
    private JSONObject jsonObject;

    public JSONObject getJSON() {return jsonObject;}
    public File getFile() {return file;}

    public ConfigUtils(String name, String subfolder) {
        this.name = name;
        filePath =  "htRewrite\\" + ((subfolder != "") ? (subfolder + "\\") : "");
        complexFilePath = filePath + name + ".json";
        File pathF = new File(filePath);
        pathF.mkdirs();
        pathF = null;
        file = new File(complexFilePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(complexFilePath);
                fileWriter.write("{}");
                fileWriter.close();
            }
        } catch(Exception e) { e.printStackTrace(); }

        try {
            InputStream is = new FileInputStream(complexFilePath);
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            jsonObject = new JSONObject(jsonTxt);
        } catch(Exception e) { e.printStackTrace(); }
    }

    public Object get(String key) { return jsonObject.get(key); }
    public void set(String key, Object value) { jsonObject.put(key, value); }

    public void save() {
        try {
            PrintWriter printWriter = new PrintWriter(complexFilePath);
            printWriter.print(jsonObject.toString());
            printWriter.close();
        }catch (Exception e) { e.printStackTrace(); }
        System.out.println("Saved config " + name);
    }
}