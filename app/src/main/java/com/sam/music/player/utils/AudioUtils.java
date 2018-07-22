package com.sam.music.player.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;


public class AudioUtils extends MediaPlayer{
    private MediaPlayer mMediaPlayer;
    private Context context;

    public AudioUtils(Context context) {
        this.context = context;

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        mMediaPlayer.setOnCompletionListener((OnCompletionListener) context);
    }

    public boolean openMedia(Object path) {
        try {

            mMediaPlayer.reset();

            if(path instanceof String) {
                AssetFileDescriptor afd = context.getResources().getAssets().openFd((String)path);
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            }else {
                mMediaPlayer.setDataSource(context, (Uri)path);
            }

            mMediaPlayer.prepare();
            mMediaPlayer.start();

            RHSLog.i("Open " + path);
            return true;
        } catch (IOException e) {
            RHSLog.e("OpenMedia: " + e.getLocalizedMessage());
        }

        return false;
    }

    public void startMedia() {
        mMediaPlayer.start();
    }

    public void stopMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void release() {
        stopMedia();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
