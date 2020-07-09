package com.bokecc.sdk.mobile.push.example.base.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.popup.SingleChoosePopup;

import butterknife.BindView;

/**
 * 作者 ${bokecc}.<br/>
 */
public class StringAdapter extends SingleChooseAdapter<StringAdapter.StringViewHolder, String> {

    public StringAdapter(Context context, SingleChoosePopup popup) {
        super(context, popup);
    }

    @Override
    protected int getItemView() {
        return R.layout.simple_item_layout;
    }

    @Override
    protected StringViewHolder getViewHolder(View itemView) {
        return new StringViewHolder(itemView);
    }

    @Override
    protected void onBind(StringViewHolder holder, int position) {
        holder.mTip.setText(mDatas.get(position));
        if (position == mPopup.getSelectedPosition()) {
            holder.mIcon.setBackgroundResource(R.drawable.simple_icon_selected);
        } else {
            holder.mIcon.setBackgroundResource(R.drawable.simple_icon_normal);
        }
    }

    public class StringViewHolder extends SingleViewHolder {

        @BindView(R.id.id_simple_item_tip)
        public TextView mTip;
        @BindView(R.id.id_simple_item_icon)
        public ImageView mIcon;

        public StringViewHolder(View itemView) {
            super(itemView);
        }
    }

}
