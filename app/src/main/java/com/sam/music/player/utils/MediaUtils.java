package com.sam.music.player.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.music.player.R;
import com.sam.music.player.RHSApp;
import com.sam.music.player.db.models.Song;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by i301487 on 4/23/16.
 */
public class MediaUtils {

    public static final int CHOOSE_EXISTING_IMAGE = 100;
    public static final int CHOOSE_EXISTING_MEDIA = 101;

    public static void chooseExistingPhoto(Activity context) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            Intent.createChooser(intent, context.getString(R.string.choose_photo));
            context.startActivityForResult(intent, CHOOSE_EXISTING_IMAGE);
        } else {
            Toast.makeText(context, R.string.media_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public static void chooseExistingAudio(Activity context) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("audio/*");

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            Intent.createChooser(intent, context.getString(R.string.choose_music));
            context.startActivityForResult(intent, CHOOSE_EXISTING_MEDIA);
        } else {
            Toast.makeText(context, R.string.media_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap decodeBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        ParcelFileDescriptor parcelFileDescriptor = null;
        String path = FileUtils.getPath(RHSApp.getAppContext(), uri);

        try {
            //Uri sometime is database document uri, we need to get disk path
            parcelFileDescriptor = RHSApp.getAppContext().getContentResolver().openFileDescriptor(Uri.fromFile(new File(path)), "r");
        } catch (Exception e) {
            return null;
        }

        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        //Is an image file
        if (options.outWidth != -1 && options.outHeight != -1) {
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            Bitmap bitmap;
            if (options.inSampleSize == 1) {
                bitmap = BitmapFactory.decodeFile(path);
            } else {
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            }

            try {
                //Close handle
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (Exception e) {

            }

            int orientation = 0;
            if (path != null) {     // remote images have a null local path. Hopefully we don't need to rotate them
                try {
                    ExifInterface ei = new ExifInterface(path);
                    orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                } catch (Exception e) {
                    RHSLog.d(e.getLocalizedMessage());
                }
            }

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotate(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotate(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotate(bitmap, 270);
                    break;
            }

            return bitmap;
        }

        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //Since there is an known issue when use ImageView.setRotation in Samsung's device, so switch to this method.
    //http://stackoverflow.com/questions/11023696/setrotation90-to-take-picture-in-portrait-mode-does-not-work-on-samsung-device
    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        bitmap.recycle();
        return rotatedBitmap;
    }

    public static void displayRoundImage(ImageView imageView, Bitmap bmp) {
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(imageView.getResources(), bmp);
        dr.setCornerRadius(Math.max(bmp.getWidth(), bmp.getHeight()) / 2.0f);
        dr.setAntiAlias(true);
        imageView.setImageDrawable(dr);
    }

    public static void displayRoundImage(ImageView imageView, int resId) {
        Bitmap bmp = BitmapFactory.decodeResource(imageView.getResources(), resId);
        displayRoundImage(imageView, bmp);
    }

    public static void displayRoundImageWithBG(ImageView imageView, int resId) {
        Bitmap bmp = drawCircleBg(BitmapFactory.decodeResource(imageView.getResources(), resId));
        displayRoundImage(imageView, bmp);
    }

    public static Bitmap drawCircleBg(Bitmap bmp) {
        Bitmap newBmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(newBmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.DKGRAY);
        paint.setAlpha(0x80);

        canvas.drawCircle(bmp.getWidth() / 2, bmp.getHeight() / 2, bmp.getWidth() / 2, paint);

        paint.setAlpha(0xff);
        canvas.drawBitmap(bmp, new Matrix(), paint);

        bmp.recycle();

        return newBmp;
    }

    public static void getEmbeddedInfo(String filepath, Song song) {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

        try {
            mediaMetadataRetriever.setDataSource(filepath);
            byte art[] = mediaMetadataRetriever.getEmbeddedPicture();
            song.cover = BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (Exception e) {
        }

        try {
            song.title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        } catch (Exception e) {
        }
    }

//    public static Bitmap getEmbeddedPicture(String filepath) {
//        Bitmap image;
//
//        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//        mediaMetadataRetriever.setDataSource(filepath);
//
//        try {
//            byte art[] = mediaMetadataRetriever.getEmbeddedPicture();
//            image = BitmapFactory.decodeByteArray(art, 0, art.length);
//        } catch (Exception e) {
//            image = null;
//        }
//        return image;
//    }
}
