package com.sam.music.player;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.sam.music.player.db.RHSStorage;
import com.sam.music.player.db.delegate.AlbumDelegate;
import com.sam.music.player.db.delegate.SongDelegate;
import com.sam.music.player.db.models.Album;
import com.sam.music.player.db.provider.AlbumProvider;
import com.sam.music.player.utils.DBParser;
import com.sam.music.player.utils.MediaUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;
import butterknife.OnLongClick;


/**
 * Created by i301487 on 12/28/15.
 */
public class HomeActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ALBUM = "album_index";

    private List<Album> albumList = new ArrayList<>();
    public static List<Album> innerAlbumList = new ArrayList<>(Arrays.asList(
            new Album(R.string.all_music, R.drawable.all_music, "0")
    ));
//    public static List<Album> innerAlbumList = new ArrayList<>(Arrays.asList(
//            new Album(R.string.kids, R.drawable.kids, "0"),
//            new Album(R.string.baby, R.drawable.baby, "1"),
//            new Album(R.string.fetal, R.drawable.fetal, "2")
//    ));

    private List<Album> dbAlbumList = new ArrayList<>();
    private Album newAlbum = new Album(R.string.add_more, R.drawable.album_bg, "-1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        validation();

        RHSStorage.getInstance().init();

        getLoaderManager().initLoader(0, null, this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, AlbumProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        albumList.clear();
        dbAlbumList.clear();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Album album = new Album();
                DBParser.getJamOData(cursor, album);
                dbAlbumList.add(album);
            }
        }

        albumList.addAll(innerAlbumList);
        if (dbAlbumList.size() > 0) {
            albumList.addAll(dbAlbumList);
        }
        albumList.add(newAlbum);

        updateView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void validation() {
        String strPkgName = getPackageName();
        if (!strPkgName.equals("com.sam.music.player"))
            finish();

//        if(!SecurityUtils.getFixedSignature().equals(SecurityUtils.getAppSignature())){
//            finish();
//        }

    }

    private void updateView() {
        LinearLayout scrollViewContainer = (LinearLayout) findViewById(R.id.horizontalScrollViewContainer);
        scrollViewContainer.removeAllViews();

        final int listSize = albumList.size();
        for (int index = 0; index < listSize; index++) {

            View view = LinearLayout.inflate(this, R.layout.home_item, null);
            final Album album = albumList.get(index);

            //Set Text
            TextView textView = (TextView) view.findViewById(R.id.album_title);
            textView.setText(album.isInner ? getString(album.titleId) : album.title);

            //Set Image
            ImageView imageView = (ImageView) view.findViewById(R.id.album_cover);
            if (album.isInner) {
                imageView.setImageResource(album.resId);
            } else {
                Bitmap bitmap = MediaUtils.decodeBitmapFromUri(album.getUri(),
                        RHSApp.getAppContext().getResources().getDimensionPixelSize(R.dimen.album_width),
                        RHSApp.getAppContext().getResources().getDimensionPixelSize(R.dimen.album_height));
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }

            }

            imageView.setTag(index);
            scrollViewContainer.addView(view);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    int tag = (Integer) view.getTag();
                    if (tag < listSize - 1) {
                        if (Integer.parseInt(album.id) == 0) {
                            //album.isInner = false;
                            intent.setClass(HomeActivity.this, AllMusicActivity.class);
                        } else {
                            intent.setClass(HomeActivity.this, MusicListActivity.class);
                        }
                    } else {
                        //Last one is the add
                        album.id = String.valueOf(tag);
                        intent.setClass(HomeActivity.this, AddAlbumActivity.class);
                    }

                    intent.putExtra(ALBUM, album);
                    startActivity(intent);
                }
            });

            if (!album.isInner) {
                ImageView ivMore = (ImageView) view.findViewById(R.id.album_action_more);
                MediaUtils.displayRoundImageWithBG(ivMore, R.drawable.ic_more_vert_white_24dp);
                ivMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleActions(album);
                    }
                });
            }
        }
    }

    private void handleActions(final Album album) {
        new AlertDialog.Builder(HomeActivity.this).setItems(R.array.edit_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //Edit
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, AddAlbumActivity.class);
                    intent.putExtra(ALBUM, album);
                    startActivity(intent);

                    FlurryAgent.onEvent(Constants.Event.EDIT_ALBUM);

                } else if (i == 1) {
                    //Delete
                    AlbumDelegate.deleteAlbum(HomeActivity.this, album.id);
                    SongDelegate.deleteAllByType(HomeActivity.this, album.id);
                    FlurryAgent.onEvent(Constants.Event.DELETE_ALBUM);
                }
            }
        }).show();
    }
}
