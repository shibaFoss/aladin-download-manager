package main.screens.music_screen;

import java.io.Serializable;

public class AudioInfo implements Serializable {
    public String filePath;
    public String songName;
    public String songArtist;
    public byte[] coverImage;

    public int audioDuration;
}
