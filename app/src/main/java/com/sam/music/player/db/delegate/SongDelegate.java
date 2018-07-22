package com.sam.music.player.db.delegate;

import android.content.ContentResolver;
import android.content.Context;

import com.sam.music.player.RHSApp;
import com.sam.music.player.db.models.Song;
import com.sam.music.player.db.provider.SongsProvider;

/**
 * Created by i301487 on 4/10/16.
 */
public class SongDelegate {
    /**
     * @param id the id in class {@link com.sam.music.player.db.models.Song}
     */
    public static void deleteSongFromList(String id) {
        ContentResolver contentResolver = RHSApp.getAppContext().getContentResolver();
        contentResolver.delete(SongsProvider.CONTENT_URI,
                Song.ID + " = ?",
                new String[]{id});
    }

    public static void deleteAllByType(Context context, String type) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(SongsProvider.CONTENT_URI, Song.TYPE + " = ?",
                new String[]{type});
    }
}
