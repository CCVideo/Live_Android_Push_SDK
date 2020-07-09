package com.bokecc.sdk.mobile.push.example.adapter;

import android.content.Context;

public abstract class SelectAdapter<VH extends BaseRecycleAdapter.BaseViewHolder, T> extends BaseRecycleAdapter<VH, T> {

    protected int mSelPosition = 0; // 选中的位置 默认第一个

    public SelectAdapter(Context context) {
        super(context);
    }

    public void setSelPosition(int selPosition) {
        mSelPosition = selPosition;
        notifyDataSetChanged();
    }
}
