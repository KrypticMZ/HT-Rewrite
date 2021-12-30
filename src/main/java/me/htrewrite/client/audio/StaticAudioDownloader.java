package me.htrewrite.client.audio;

import me.htrewrite.client.util.PostRequest;

import java.io.File;

public class StaticAudioDownloader {
    public static void downloadAllAudios() {
        String mainSoundResourcesFolder = "htRewrite\\resources\\sound\\";
        String vocalsFolder = mainSoundResourcesFolder+"vocals\\";
        String musicFolder = mainSoundResourcesFolder+"music\\";

        File MSRFFile = new File(mainSoundResourcesFolder);
        File VFFile = new File(vocalsFolder);
        File MFFile = new File(musicFolder);

        MSRFFile.mkdirs();
        VFFile.mkdirs();
        MFFile.mkdirs();

        /* CHECK */
        String VAString = vocalsFolder+"auth.mp3";
        String VASString = vocalsFolder+"auth_success.mp3";
        String LSString = vocalsFolder+"load_success.mp3";
        String MString = musicFolder+"main.mp3";

        File VAFile = new File(VAString);
        File VASFile = new File(VASString);
        File LSFile = new File(LSString);

        File MFile = new File(MString);

        if(!VAFile.exists())
            PostRequest.downloadFile("https://aurahardware.eu/api/resources/vocals/auth.mp3", VAString);
        if(!VASFile.exists())
            PostRequest.downloadFile("https://aurahardware.eu/api/resources/vocals/auth_success.mp3", VASString);
        if(!LSFile.exists())
            PostRequest.downloadFile("https://aurahardware.eu/api/resources/vocals/load_success.mp3", LSString);

        if(!MFile.exists())
            PostRequest.downloadFile("https://aurahardware.eu/api/resources/music/main.mp3", MString);
    }
}