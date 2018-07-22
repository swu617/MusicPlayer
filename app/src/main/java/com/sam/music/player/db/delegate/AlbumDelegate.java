package com.sam.music.player.db.delegate;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.sam.music.player.db.models.Album;
import com.sam.music.player.db.provider.AlbumProvider;
import com.sam.music.player.utils.DBParser;

/**
 * Created by i301487 on 5/3/16.
 */
public class AlbumDelegate {

    public static void updateAlbum(Context context, Album album) {

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues value = DBParser.populateValues(album);

        if (value.size() > 0) {
            contentResolver.update(AlbumProvider.CONTENT_URI, value, Album.ID + " = ? ", new String[]{
                    String.valueOf(album.id)
            });
        }
    }

    public static void deleteAlbum(Context context, String id) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(AlbumProvider.CONTENT_URI, Album.ID + " = ? ", new String[]{id});
    }
}
