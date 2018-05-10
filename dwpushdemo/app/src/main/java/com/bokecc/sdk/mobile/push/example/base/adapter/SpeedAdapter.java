package com.bokecc.sdk.mobile.push.example.base.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.entity.SpeedRtmpNode;
import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.popup.SingleChoosePopup;
import com.bokecc.sdk.mobile.push.tools.LogUtil;

import butterknife.BindView;

/**
 * 作者 ${郭鹏飞}.<br/>
 */
public class SpeedAdapter extends SingleChooseAdapter<SpeedAdapter.SpeedViewHolder, SpeedRtmpNode> {

    public SpeedAdapter(Context context, SingleChoosePopup popup) {
        super(context, popup);
    }

    @Override
    protected int getItemView() {
        return R.layout.speed_result_layout;
    }

    @Override
    protected SpeedViewHolder getViewHolder(View itemView) {
        return new SpeedViewHolder(itemView);
    }

    @Override
    protected void onBind(SpeedViewHolder holder, int position) {
        SpeedRtmpNode rtmpNode = mDatas.get(position);
        holder.mDesc.setText(rtmpNode.getDesc());

        // 如果rtmp推流节点是备用节点，不做测速展示
        if (!rtmpNode.isSpareNode()) {
            if (!rtmpNode.isNGBMode()) {
                long connectTime = rtmpNode.getConnectTime();
                holder.mConnectTime.setText(connectTime >= 500 ? "超时" : String.valueOf(connectTime));
            }
            holder.mRecommend.setText(rtmpNode.isRecommend() ? "推荐" : "");
        } else {
            LogUtil.e("DwPush SpeedAdapter", "备用节点 ：" + rtmpNode.getDesc() + "  地址：" + rtmpNode.getRtmpPath());
        }

        if (position == mPopup.getSelectedPosition()) {
            holder.mIcon.setBackgroundResource(R.drawable.simple_icon_selected);
        } else {
            holder.mIcon.setBackgroundResource(R.drawable.simple_icon_normal);
        }
    }

    public class SpeedViewHolder extends SingleViewHolder {

        @BindView(R.id.id_speed_result_desc)
        public TextView mDesc;
        @BindView(R.id.id_speed_result_connect_time)
        public TextView mConnectTime;
        @BindView(R.id.id_speed_result_recommend)
        public TextView mRecommend;
        @BindView(R.id.id_speed_result_icon)
        public ImageView mIcon;

        public SpeedViewHolder(View itemView) {
            super(itemView);
        }

    }

}
