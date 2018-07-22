package com.sam.music.player.db.provider;

import android.content.Context;
import android.net.Uri;

import com.sam.music.player.db.RHSStorage;
import com.sam.music.player.db.models.Album;

import net.sqlcipher.database.SQLiteDatabase;

public class AlbumProvider extends SimpleTableProvider {
    public static final String TABLE_NAME = "album";
    public static final Uri CONTENT_URI = RHSStorage.CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
    private final String CREATE_TABLE_QUERY = getCreateTableSql(Album.class);

    public AlbumProvider(Context context) {
        super(context);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getUniqueColumns() {
        return Album.ID;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }
}
