package com.bokecc.sdk.mobile.push.example.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bokecc.sdk.mobile.push.example.popup.SingleChoosePopup;

import java.util.ArrayList;

/**
 * 作者 ${bokecc}.<br/>
 */
public abstract class SingleChooseAdapter<VH extends SingleViewHolder, T> extends RecyclerView.Adapter<VH> {

    private Context mContext;
    ArrayList<T> mDatas; // 数据

    SingleChoosePopup mPopup;

    SingleChooseAdapter(Context context, SingleChoosePopup popup) {
        mContext = context;
        mPopup = popup;
        mDatas = new ArrayList<>();
    }

    public void bindDatas(ArrayList<T> datas) {
        mDatas = datas;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).
                inflate(getItemView(), parent, false);
        return getViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setTag(position); // 使用位置作为当前item布局的标签
        onBind(holder, position);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    protected abstract int getItemView();

    protected abstract VH getViewHolder(View itemView);

    protected abstract void onBind(VH holder, int position);

}
