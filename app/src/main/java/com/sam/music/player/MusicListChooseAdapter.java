/**
 * Created by i301487 on 6/30/16.
 * Copyright (c) 2016 SAP. All rights reserved.
 */
package com.sam.music.player;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sam.music.player.db.models.Album;
import com.sam.music.player.db.models.Song;
import com.sam.music.player.utils.FileUtils;
import com.sam.music.player.utils.MediaUtils;
import com.sam.music.player.utils.RegxUrils;

import java.util.List;

public class MusicListChooseAdapter extends RecyclerView.Adapter<MusicListChooseAdapter.ViewHolder> {
    private List<Song> musicList;

    public MusicListChooseAdapter(List<Song> musicList, Album album) {
        super();
        this.musicList = musicList;
    }

    public void setMusicList(List<Song> musicList){
        this.musicList = musicList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Song song = musicList.get(position);
        String title = null;
        if (song.uri != null) {
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
        }

        holder.checkBox.setChecked(song.isChecked);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                song.isChecked = b;
            }
        });

        holder.textView.setText(title);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_choose_item, parent, false);

        final ViewHolder viewHolder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean isChecked = viewHolder.checkBox.isChecked();
                viewHolder.checkBox.setChecked(!isChecked);
            }
        });

        return new ViewHolder(v);
    }

    public void update() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return musicList == null ? 0 : musicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            imageView = (ImageView) itemView.findViewById(R.id.ivMusic);
            textView = (TextView) itemView.findViewById(R.id.tvMusic);
        }
    }
}
