package com.bokecc.sdk.mobile.push.example.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bokecc.sdk.mobile.push.example.R;

/**
 * 表情适配器
 *
 * @author CC
 */
public class EmojiAdapter extends CommonArrayAdapter<Integer> {

    public EmojiAdapter(Context context) {
        super(context);
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int position) {
        ImageView emoji = viewHolder.getView(R.id.id_item_emoji);
        emoji.setImageResource(datas[position]);
    }

    @Override
    protected int getItemViewId() {
        return R.layout.item_emoji;
    }
}
