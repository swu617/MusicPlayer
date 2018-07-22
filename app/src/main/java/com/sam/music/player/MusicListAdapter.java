package com.sam.music.player;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sam.music.player.db.models.Album;
import com.sam.music.player.db.models.Song;
import com.sam.music.player.utils.FileUtils;
import com.sam.music.player.utils.MediaUtils;
import com.sam.music.player.utils.RHSLog;
import com.sam.music.player.utils.RegxUrils;

import java.util.List;

/**
 * Created by i301487 on 1/3/16.
 */
public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private List<Song> musicList;
    private Album album;
    private int selectedItem = -1;
    private int defaultColor;

    public MusicListAdapter(List<Song> musicList, Album album) {
        super();
        this.musicList = musicList;
        this.album = album;

        defaultColor = ContextCompat.getColor(RHSApp.getAppContext(), R.color.textColor);

        //initBitmap();
    }



//    private void initBitmap() {
//        try {
//            if (album.getUri() != null) {
//                defaultBitmap = MediaUtils.decodeBitmapFromUri(album.getUri(),
//                        60,
//                        60);
//            }
//        } catch (Exception e) {
//            RHSLog.d(e.getLocalizedMessage());
//        }
//    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Song song = musicList.get(position);
        String title = null;
        if (song.uri != null) {
            if (!song.isLocal) {
                String path = FileUtils.getPath(RHSApp.getAppContext(), Uri.parse(song.uri));
                if (song.cover == null || song.title == null) {
                    MediaUtils.getEmbeddedInfo(path, song);
                }

                if (!TextUtils.isEmpty(song.title)) {
                    title = song.title;
                } else {
                    title = RegxUrils.getFileName(path);
                }

                if (song.cover != null) {
                    MediaUtils.displayRoundImage(holder.imageView, song.cover);
                } else {
                    holder.imageView.setImageResource(R.drawable.icon);
                }
            } else {
                MediaUtils.displayRoundImage(holder.imageView, album.resId);
                title = RegxUrils.getFileName(song.uri);
            }
        }

        //RHSLog.i(title);

        holder.textView.setText(title);
        holder.textView.setTextColor(selectedItem == position ? Color.RED : defaultColor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.textView.setTextColor(Color.RED);
                selectedItem = viewHolder.getLayoutPosition();
                update();

                ((MusicListActivity) view.getContext()).onItemClick();
            }
        });

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MusicListActivity) view.getContext()).onItemLongClick(viewHolder.getLayoutPosition());
                return false;
            }
        });
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return musicList == null ? 0 : musicList.size();
    }

    public void update() {
        this.notifyDataSetChanged();
    }

    public int getCurrentPosition() {
        return selectedItem;
    }

    public void setCurrentPosition(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void next() {
        selectedItem++;
        if (selectedItem >= getItemCount())
            selectedItem = 0;

        update();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivMusic);
            textView = (TextView) itemView.findViewById(R.id.tvMusic);
        }
    }
}
