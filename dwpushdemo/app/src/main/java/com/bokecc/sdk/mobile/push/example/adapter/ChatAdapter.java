package com.bokecc.sdk.mobile.push.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.model.ChatEntity;
import com.bokecc.sdk.mobile.push.example.util.EmojiUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context mContext;
    private ArrayList<ChatEntity> mChatEntities;

    public ChatAdapter(Context context) {
        mContext = context;
    }

    /**
     * 设置聊天的数据源
     */
    public void setChatEntities(ArrayList<ChatEntity> chatEntities) {
        mChatEntities = chatEntities;
    }

    public ArrayList<ChatEntity> getChatEntities() {
        return mChatEntities;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.push_chat_item_layout, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatEntity chatEntity = mChatEntities.get(position);
        StringBuilder content = new StringBuilder();
        if (chatEntity.isPrivate()) {
            content.append("@");
            content.append(chatEntity.getReceivedUserName());
            content.append(" ");
        }
        content.append(chatEntity.getMsg());
        String msg = chatEntity.getUserName() + "：" + content.toString();
        SpannableString ss = new SpannableString(msg);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#fff5b108")),
                0, chatEntity.getUserName().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (chatEntity.isPublisher()) {
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFF6633")),
                    chatEntity.getUserName().length() + 1,
                    msg.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFFFF")),
                    chatEntity.getUserName().length() + 1,
                    msg.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.mContent.setText(EmojiUtil.parseFaceMsg(mContext, ss));
    }

    @Override
    public int getItemCount() {
        return mChatEntities == null ? 0 : mChatEntities.size();
    }

    final class ChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_chat_item_content)
        TextView mContent;

        ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
