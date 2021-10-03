package MusicPlayer;

//using jaco's mp3player jar
import jaco.mp3.player.MP3Player;


import java.io.*;
import java.net.URL;
import java.util.*;

public class mp3Player {
    MP3Player player;

    public mp3Player(){
        player = new MP3Player();
    }

    // button actions
    public void play(){
        player.play();
    }

    public void pause(){
        player.pause();
    }

    public void stop(){
        player.stop();
    }

    public void skipForward(){
        player.skipForward();
    }

    public void skipBackward(){
        player.skipBackward();
    }

    public void addToPlaylist(File file){
        player.addToPlayList(file);
    }

    public void setShuffle(boolean bool){
        player.setShuffle(bool);
    }

    public void setRepeat(boolean repeat){
        player.setRepeat(repeat);
    }

    //get info
    public boolean isPaused(){
        return player.isPaused();
    }

    public boolean isStopped(){
        return player.isStopped();
    }

    public List<URL> getPlayList(){
        return player.getPlayList();
    }

    public boolean isShuffle(){
        return player.isShuffle();
    }

    public boolean isRepeat(){
        return player.isRepeat();
    }
}

