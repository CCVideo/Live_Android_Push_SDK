package com.bokecc.sdk.mobile.push.example.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * 作者 ${郭鹏飞}.<br/>
 */
public class SingleViewHolder extends RecyclerView.ViewHolder {

    public SingleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
