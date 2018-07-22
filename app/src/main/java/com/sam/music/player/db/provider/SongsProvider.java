package com.sam.music.player.db.provider;

import android.content.Context;
import android.net.Uri;

import com.sam.music.player.db.RHSStorage;
import com.sam.music.player.db.models.Song;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by i301487 on 12/30/15.
 */
public class SongsProvider extends SimpleTableProvider {

    public static final String TABLE_NAME = "songs";
    public static final Uri CONTENT_URI = RHSStorage.CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
    private final String CREATE_TABLE_QUERY = getCreateTableSql(Song.class);

    public SongsProvider(Context context) {
        super(context);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getUniqueColumns() {
        return Song.ID;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }
}
