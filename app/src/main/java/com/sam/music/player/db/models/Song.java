package com.sam.music.player.db.models;

import android.graphics.Bitmap;

/**
 * Created by i301487 on 12/28/15.
 */
public class Song extends ODataItem<Song> {

    public static final String ID = "id";

    public static final String URI = "uri";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String LOCAL = "local";

    public static final String IS_FREE = "free";
    public static final String IS_PURCHASED = "purchase";
    public static final String LYRICS = "lyrics";
    public static final String ACCESSED_TIME = "accessed_time";

    @Column(ID)
    public String id;

    @Column(URI)
    public String uri;

    @Column(TYPE)
    public int type;

    @Column(NAME)
    public String fileName;

    public String title;

    public Bitmap cover;

    @Column(LOCAL)
    public boolean isLocal;

    @Column(LYRICS)
    public String lyrics;

    @Column(IS_FREE)
    public boolean isFree;

    @Column(ACCESSED_TIME)
    public String accessedTime;

    public boolean isChecked;


}
