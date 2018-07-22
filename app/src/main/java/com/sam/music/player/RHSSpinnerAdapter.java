/*
 * Created by i301487 on 2015/4/30
 * Copyright (c) 2015 SAP. All rights reserved.
 */

package com.sam.music.player;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RHSSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {
    private final String TAG_CLOSED = "CLOSED";
    private final String TAG_DROPDOWN = "DROPDOWN";
    private Context context;
    private List<String> timeList = new ArrayList<>();
    private int selectedItem;
    private int lockedItemsLength;

    public RHSSpinnerAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<String> timeList) {
        this.timeList.clear();
        this.timeList.addAll(timeList);
    }

    public void setLockedItemsLength(int lockedItemsLength) {
        this.lockedItemsLength = lockedItemsLength;
    }

    public int getSelectedItem() {
        return this.selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public int getCount() {
        return timeList != null ? timeList.size() : 0;
    }

    @Override
    public String getItem(int position) {
        return timeList != null ? timeList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || !convertView.getTag().toString().equals(TAG_CLOSED)) {
            convertView = View.inflate(context, R.layout.spinner_closed, null);
            convertView.setTag(TAG_CLOSED);
        }

        TextView title = (TextView) convertView.findViewById(R.id.spinner_closed_text);
        String loopIndicator = timeList.get(position);
        title.setText(loopIndicator);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null || !convertView.getTag().toString().equals(TAG_DROPDOWN)) {
            convertView = View.inflate(context, R.layout.spinner_item_dropdown, null);
            convertView.setTag(TAG_DROPDOWN);
        }

        convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = (TextView)convertView.findViewById(R.id.spinner_text);
        tv.setText(timeList.get(position));

        return convertView;
    }
}
