package com.sam.music.player;

import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.content.Loader;

import com.sam.music.player.db.models.Song;

/**
 * Created by i301487 on 6/8/16.
 */
public class AllMusicActivity extends MusicListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(album.getTitle());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.IS_MUSIC
                },
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        musicList.clear();
        addedList.clear();

        if (cursor != null && cursor.getCount() > 0) {

            boolean bIsMusic;
            long fileSize;
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                song.uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                fileSize = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                bIsMusic = (cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) == 1);

                if (bIsMusic && fileSize > 10000) {
                    //Limit music size greater than 10K.
                    addedList.add(song);
                }
            }
        }

        if (addedList.size() > 0) {
            musicList.addAll(addedList);
        }
        musicListAdapter.update();

        updateListView();
    }

    @Override
    public void onItemLongClick(final int position) {
        //Do nothing
    }
}

