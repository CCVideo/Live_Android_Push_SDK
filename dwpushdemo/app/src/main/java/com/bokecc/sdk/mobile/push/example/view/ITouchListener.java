package com.bokecc.sdk.mobile.push.example.view;

import android.support.v7.widget.RecyclerView;

public interface ITouchListener {
    void onClick(RecyclerView.ViewHolder viewHolder);

    void onLongPress(RecyclerView.ViewHolder viewHolder);

    void onTouchDown(RecyclerView.ViewHolder viewHolder);

    void onTouchUp(RecyclerView.ViewHolder viewHolder);
}

