package com.bokecc.sdk.mobile.push.example.adapter;

import android.content.Context;
import android.view.View;

import com.bokecc.sdk.mobile.push.entity.SpeedRtmpNode;
import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.view.SettingItemLayout;

import butterknife.BindView;

public class ServerRecycleAdapter extends SelectAdapter<ServerRecycleAdapter.ServerViewHolder, SpeedRtmpNode> {

    public ServerRecycleAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(ServerViewHolder holder, int position) {
        holder.mServer.setTip(mDatas.get(position).getDesc());
        long time = mDatas.get(position).getConnectTime();

        // 非备用节点才显示测速
        if (!mDatas.get(position).isSpareNode()) {
            holder.mServer.setValue(time > 500 || time <= 0 ? "超时" : String.valueOf(time) + "ms");
        }

        if (position == mSelPosition) {
            holder.mServer.setLeftImageResource(R.drawable.select_icon_selected);
        } else {
            holder.mServer.setLeftImageResource(R.drawable.select_icon_normal);
        }
    }

    @Override
    public int getItemView() {
        return R.layout.item_server;
    }

    @Override
    public ServerViewHolder getViewHolder(View itemView) {
        return new ServerViewHolder(itemView);
    }

    public static class ServerViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_server_item)
        SettingItemLayout mServer;

        public ServerViewHolder(View itemView) {
            super(itemView);
        }
    }

}
