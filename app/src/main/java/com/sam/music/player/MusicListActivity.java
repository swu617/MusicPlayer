package com.sam.music.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sam.music.player.db.delegate.SongDelegate;
import com.sam.music.player.db.models.Album;
import com.sam.music.player.db.models.Song;
import com.sam.music.player.db.provider.SongsProvider;
import com.sam.music.player.utils.DBParser;
import com.sam.music.player.utils.AudioUtils;
import com.sam.music.player.utils.FileUtils;
import com.sam.music.player.utils.MediaUtils;
import com.sam.music.player.utils.OnItemClickListener;
import com.sam.music.player.utils.RHSLog;
import com.sam.music.player.utils.RHSPref;
import com.sam.music.player.utils.RegxUrils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MusicListActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, OnItemClickListener,
        AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private List<String> folderList = new ArrayList<>(Arrays.asList(
            "kids", "baby", "fetal"
    ));

    private List<Song> innerList = new ArrayList<>(); //List for inner music list
    protected List<Song> musicList = new ArrayList<>(); //List for displaying list which include innerList and addedList;
    protected List<Song> addedList = new ArrayList<>(); //List for user added list
    protected MusicListAdapter musicListAdapter;
    protected Album album;

    private String[] timeList;
    private AudioUtils audioUtils;
    private boolean playAllMode;

    private Handler timeHandler = new Handler();
    private Handler adHandler = new Handler();
    private Runnable runnable;

    @Bind(R.id.action)
    Button btnAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.music_list);

        album = getIntent().getParcelableExtra(HomeActivity.ALBUM);
        setTitle(album.getTitle());

        ButterKnife.bind(this);

        if (album.isInner) {
            loadInnerMusicList(Integer.parseInt(album.id));
        }

        initViews();

        audioUtils = new AudioUtils(this);

        loadAds();

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                MediaUtils.chooseExistingAudio(this);
                break;
            case R.id.menu_item_share:
                shareTo();
                FlurryAgent.onEvent(Constants.Event.SHARE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.action)
    public void playOrStop() {
        boolean bStop = (boolean) btnAction.getTag();
        if (bStop) {
            stopPlay();
        } else {
            if (musicListAdapter.getCurrentPosition() == -1) {
                musicListAdapter.setCurrentPosition(0);
                musicListAdapter.update();
            }
            startPlay();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case Activity.RESULT_OK:
                switch (requestCode) {
                    case MediaUtils.CHOOSE_EXISTING_MEDIA: {
                        //Save uri to database.
                        final Song song = new Song();
                        song.type = Integer.parseInt(album.id);
                        song.uri = data.getData().toString();

                        song.id = album.id + song.uri.hashCode();
                        DBParser.saveJamData(this, song, SongsProvider.CONTENT_URI);

                        FlurryAgent.onEvent(Constants.Event.ADDED_SONG, new HashMap<String, String>() {{
                            put(album.id, RegxUrils.getFileName(FileUtils.getPath(RHSApp.getAppContext(), Uri.parse(song.uri))));
                        }});
                        break;
                    }
                }
        }
    }

    //https://play.google.com/store/apps/details?id=com.sam.music.player
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        //Spinner settings change
        int spinnerId = adapterView.getId();
        if (spinnerId == R.id.spinner_play) {
            playAllMode = (pos == 0);
            audioUtils.setLooping(!playAllMode);
            RHSPref.write(Constants.PLAY_MODE, pos);
        } else if (spinnerId == R.id.spinner_time) {
            RHSPref.write(Constants.LOOP_TIME, pos);
            if (audioUtils.isPlaying()) {
                startTimer();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Not used for spinner.
    }

    @Override
    public void onItemClick() {
        startPlay();
    }

    @Override
    public void onItemLongClick(final int position) {
        if (position < innerList.size()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_song_title)
                .setMessage(R.string.delete_song_msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (position == musicListAdapter.getCurrentPosition()) {
                            stopPlay();
                        }

                        Song song = musicList.get(position);
                        SongDelegate.deleteSongFromList(song.id);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, SongsProvider.CONTENT_URI, null,
                Song.TYPE + " = ?", new String[]{album.id}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        musicList.clear();
        addedList.clear();
        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                Song song = new Song();
                DBParser.getJamOData(cursor, song);
                addedList.add(song);
            }
        }


        if (album.isInner) {
            musicList.addAll(innerList);
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
    public void onCompletion(MediaPlayer mp) {
        if (!playAllMode)
            return;

        musicListAdapter.next();
        audioUtils.openMedia(getFilePath());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioUtils != null) {
            audioUtils.release();
            stopTimer();
        }

        adHandler.removeCallbacksAndMessages(null);
    }

    private void initViews() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        musicListAdapter = new MusicListAdapter(musicList, album);
        recyclerView.setAdapter(musicListAdapter);

        btnAction.setTag(false);

        //Set Spinner for play mode.
        Spinner spinnerPlayMode = (Spinner) findViewById(R.id.spinner_play);
        RHSSpinnerAdapter spinnerAdapterMode = new RHSSpinnerAdapter(this);
        spinnerAdapterMode.setList(Arrays.asList(getResources().getStringArray(R.array.play_mode)));

        spinnerPlayMode.setAdapter(spinnerAdapterMode);
        spinnerPlayMode.setSelection(RHSPref.readInt(Constants.PLAY_MODE));
        spinnerPlayMode.setOnItemSelectedListener(this);

        //Set Spinner for time settings.
        Spinner spinnerTime = (Spinner) findViewById(R.id.spinner_time);
        RHSSpinnerAdapter spinnerAdapterTime = new RHSSpinnerAdapter(this);

        timeList = getResources().getStringArray(R.array.time_list);
        spinnerAdapterTime.setList(Arrays.asList(timeList));

        spinnerTime.setAdapter(spinnerAdapterTime);
        int selected = RHSPref.readInt(Constants.LOOP_TIME);
        if (selected == -1) {
            selected = 6;
        }
        spinnerTime.setSelection(selected);
        spinnerTime.setOnItemSelectedListener(this);
    }

    protected void updateListView() {

        TextView noMedia = (TextView) findViewById(R.id.no_media);
        View recyclerView = findViewById(R.id.recycler_view);
        if (musicList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            btnAction.setVisibility(View.VISIBLE);
            noMedia.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            btnAction.setVisibility(View.INVISIBLE);
            noMedia.setVisibility(View.VISIBLE);
        }
    }

    //https://developers.google.com/admob/android/targeting#birthday
    //https://developer.android.com/distribute/googleplay/families/about.html
    private void loadAds() {
        Runnable runnableAd = new Runnable() {
            @Override
            public void run() {

                RHSLog.i("start ad=========");
                AdView mAdView = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

                mAdView.setVisibility(View.VISIBLE);
            }
        };

        adHandler.postDelayed(runnableAd, 6000);
    }

    private void loadInnerMusicList(int index) {
        try {

            if(index == 0){
                return;
            }

            String[] list = getResources().getAssets().list(folderList.get(index -1));
            for (int i = 0; i < list.length; i++) {
                Song song = new Song();
                song.uri = list[i];
                song.isLocal = true;

                song.id = album.id + song.uri.hashCode();
                innerList.add(song);
            }

            musicList.addAll(innerList);

        } catch (IOException e) {
            RHSLog.e(e.getLocalizedMessage());
        }
    }

    private void startPlay() {
        audioUtils.openMedia(getFilePath());
        audioUtils.setLooping(!playAllMode);
        startTimer();

        btnAction.setBackgroundResource(R.drawable.btn_stop);
        btnAction.setTag(true);
    }

    private void stopPlay() {
        stopTimer();
        audioUtils.stopMedia();

        btnAction.setBackgroundResource(R.drawable.btn_continue);
        btnAction.setTag(false);
    }

    private void startTimer() {

        stopTimer();

        int interval = getLoopTimeSettings();
        if (interval == 0) {
            //interval is 0 means the settings is forever.
            return;
        }

        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    stopPlay();
                }
            };
        }

        timeHandler.postAtTime(runnable, System.currentTimeMillis() + interval);
        timeHandler.postDelayed(runnable, interval);
    }

    private void stopTimer() {
        if (runnable != null) {
            timeHandler.removeCallbacks(runnable);
        }
    }

    int getLoopTimeSettings() {
        int pos = RHSPref.readInt(Constants.LOOP_TIME);
        if (pos < 3) {
            return (pos + 1) * Constants.MIN * 15;
        } else if (pos < 6) {
            return (pos + 1) * Constants.HR;
        }
        return 0;
    }

    private Object getFilePath() {
        Song song = musicList.get(musicListAdapter.getCurrentPosition());
        if (song.isLocal) {
            return folderList.get(Integer.parseInt(album.id) -1) + "/" + song.uri;
        } else {
            String path = FileUtils.getPath(RHSApp.getAppContext(), Uri.parse(song.uri));
            return Uri.parse(path);
        }
    }

    private void shareTo() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.share_link));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_to)));
    }
}
