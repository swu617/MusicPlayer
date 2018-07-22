package com.sam.music.player;

public class Constants {

    public final static String CY_EN = "All copyrights reserved by Shaoming Wu. Any copying, transferring or any other usage is prohibited. Or else, Shaoming Wu possesses the right to require legal responsibilities from the violator.\nEmail:wsm617@126.com";

    //For settings
    public final static String PLAY_MODE = "play_mode";
    public final static String LOOP_TIME = "loop_time";

    //For time setting
    public final static int MIN = 60 * 1000;
    public final static int HR = 60 * 60 * 1000;

    public final static class Event {
        public final static String SHARE = "share_song";
        public final static String ADDED_SONG = "added_song";
        public final static String ADD_ALBUM = "add_album";
        public final static String EDIT_ALBUM = "edit_album";
        public final static String DELETE_ALBUM = "delete_album";
    }

}
