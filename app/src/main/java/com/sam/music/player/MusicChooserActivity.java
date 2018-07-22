/**
 * Created by i301487 on 6/30/16.
 * Copyright (c) 2016 SAP. All rights reserved.
 */
package com.sam.music.player;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.sam.music.player.db.delegate.AlbumDelegate;
import com.sam.music.player.db.models.Album;
import com.sam.music.player.db.models.Song;
import com.sam.music.player.db.provider.AlbumProvider;
import com.sam.music.player.db.provider.SongsProvider;
import com.sam.music.player.utils.DBParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicChooserActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected List<Song> musicList = new ArrayList<>(); //List for displaying list which include innerList and addedList;
    protected List<Song> addedList = new ArrayList<>(); //List for user added list
    protected MusicListChooseAdapter musicListAdapter;
    private Album album = new Album();

    private boolean isEditMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.music_list_choose);
        this.setTitle(R.string.choose_music);

        album = getIntent().getParcelableExtra(HomeActivity.ALBUM);
        Uri uri = album.getUri();
        if (uri != null) {
            isEditMode = true;
        }

        initViews();

        getLoaderManager().initLoader(0, null, this);
    }

    private void initViews() {

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_chooser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        musicListAdapter = new MusicListChooseAdapter(musicList, album);
        recyclerView.setAdapter(musicListAdapter);

        CheckBox checkAll = (CheckBox) findViewById(R.id.check_all);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                int childCount = musicListAdapter.getItemCount();
                for (int i = 0; i < childCount; i++) {
                    Song song = musicList.get(i);
                    song.isChecked = checked;
                }
                musicListAdapter.setMusicList(musicList);
            }
        });


    }

    protected void updateListView() {

        TextView noMedia = (TextView) findViewById(R.id.no_choose_media);
        View recyclerView = findViewById(R.id.rv_chooser);
        if (musicList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noMedia.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            noMedia.setVisibility(View.VISIBLE);
        }
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

                if (bIsMusic && fileSize > 1000) {
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
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        menu.getItem(1).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done: {

                //if (validateInput())
                {
                    //Save album informaiton
                    if (isEditMode) {
                        AlbumDelegate.updateAlbum(this, album);
                    } else {
                        DBParser.saveJamData(this, album, AlbumProvider.CONTENT_URI);
                        //launchMusicList();
                    }

                    //Save music list information


                    FlurryAgent.onEvent(Constants.Event.ADD_ALBUM, new HashMap<String, String>() {{
                        put(String.valueOf(album.id), album.title);
                    }});
                }

                //Todo: save all the music...
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMusicList() {
        if (isEditMode) {

        } else {
            Song songChecked = new Song();
            for (Song song : musicList) {
                if (song.isChecked) {
                    songChecked.itemList.add(song);
                }
            }
            DBParser.saveJamData(this, songChecked, SongsProvider.CONTENT_URI);

            //Todo:nedd to verify it later....
        }
    }
}
