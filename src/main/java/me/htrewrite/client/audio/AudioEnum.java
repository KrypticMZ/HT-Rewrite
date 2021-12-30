package me.htrewrite.client.audio;

import jaco.mp3.player.MP3Player;

import java.io.File;

public class AudioEnum {
    public enum Vocals {
        AUTH("auth.mp3"), AUTH_SUCCESS("auth_success.mp3"),
        LOAD_SUCCESS("load_success.mp3");

        public final String fileName;
        public final File file;
        private MP3Player mp3Player;
        Vocals(String fileName) {
            this.fileName = fileName;
            this.file = new File("htRewrite\\resources\\sound\\vocals\\"+fileName);
            this.mp3Player = new MP3Player(file);
            mp3Player.setRepeat(false);
        }

        public void play() { mp3Player.play(); }
        public void stop() { mp3Player.stop(); }
    }
    public enum Music {
        MAIN("main.mp3");

        public final String fileName;
        public final File file;
        public final MP3Player mp3Player;
        Music(String fileName) {
            this.fileName = fileName;
            this.file = new File("htRewrite\\resources\\sound\\music\\"+fileName);
            this.mp3Player = new MP3Player(file);
            mp3Player.setRepeat(true);
        }

        public void play() { mp3Player.play(); }
        public void stop() { mp3Player.stop(); }
        public void pause() { mp3Player.pause(); }
        public void setRepeat(boolean repeat) { mp3Player.setRepeat(repeat); }
    }
}