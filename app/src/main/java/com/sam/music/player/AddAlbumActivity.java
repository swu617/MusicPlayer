package com.sam.music.player;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sam.music.player.db.delegate.AlbumDelegate;
import com.sam.music.player.db.models.Album;
import com.sam.music.player.db.provider.AlbumProvider;
import com.sam.music.player.utils.DBParser;
import com.sam.music.player.utils.MediaUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by i301487 on 4/23/16.
 */
public class AddAlbumActivity extends BaseActivity {

    @BindView(R.id.bg_album)
    ImageView ivAlbum;

    private Album album = new Album();
    //private boolean isEditMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_album);
        ButterKnife.bind(this);

        album = getIntent().getParcelableExtra(HomeActivity.ALBUM);

        initViews();

    }

    private void initViews() {
        if (album.title != null && album.title.length() > 0) {
            EditText etName = (EditText) findViewById(R.id.et_name);
            etName.setText(album.title);
        }

        Uri uri = album.getUri();
        if (uri != null) {
            setAlbumBg(album);
        }
    }

    @OnClick(R.id.bg_album)
    public void choosePhoto() {
        MediaUtils.chooseExistingPhoto(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_item_done:
//                if (validateInput()) {
//                    if (isEditMode) {
//                        AlbumDelegate.updateAlbum(this, album);
//                    } else {
//                        DBParser.saveJamData(this, album, AlbumProvider.CONTENT_URI);
//                        //launchMusicList();
//                    }
//
//                    FlurryAgent.onEvent(Constants.Event.ADD_ALBUM, new HashMap<String, String>() {{
//                        put(String.valueOf(album.id), album.title);
//                    }});
//
//                    finish();
//                    return true;
//                }
//                break;
            case R.id.menu_item_next:
                //if (validateInput())
                {
                    launchMusicChooserList();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case Activity.RESULT_OK:
                switch (requestCode) {
                    case MediaUtils.CHOOSE_EXISTING_IMAGE: {
                        //Save uri to database.
                        album.uri = data.getData().toString();
                        setAlbumBg(album);
                        break;
                    }
                }
        }
    }

    private boolean validateInput() {
        EditText etName = (EditText) findViewById(R.id.et_name);
        String name = etName.getText().toString().trim();
        if (name.length() == 0) {
            Toast.makeText(this, R.string.invalidate_text, Toast.LENGTH_LONG).show();
            return false;
        }

        album.title = name;

        if (album.uri == null) {
            Toast.makeText(this, R.string.invalidate_photo, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void setAlbumBg(final Album albumTemp) {
        Bitmap bitmap = MediaUtils.decodeBitmapFromUri(albumTemp.getUri(),
                RHSApp.getAppContext().getResources().getDimensionPixelSize(R.dimen.album_width),
                RHSApp.getAppContext().getResources().getDimensionPixelSize(R.dimen.album_height));
        if (bitmap != null) {
            ivAlbum.setImageBitmap(bitmap);
        }
    }

    private void launchMusicChooserList() {
        Intent intent = new Intent();
        intent.setClass(this, MusicChooserActivity.class);
        intent.putExtra(HomeActivity.ALBUM, album);
        startActivity(intent);
    }
}
