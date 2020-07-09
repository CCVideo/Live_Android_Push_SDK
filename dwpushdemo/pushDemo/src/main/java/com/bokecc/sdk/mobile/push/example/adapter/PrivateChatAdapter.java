package com.bokecc.sdk.mobile.push.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.model.ChatEntity;
import com.bokecc.sdk.mobile.push.example.util.EmojiUtil;
import com.bokecc.sdk.mobile.push.example.view.HeadView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivateChatAdapter extends RecyclerView.Adapter<PrivateChatAdapter.PrivateChatViewHolder> {

    private static final int ITEM_TYPE_COME = 0;
    private static final int ITEM_TYPE_SELF = 1;

    private Context mContext;
    private ArrayList<ChatEntity> mChatEntities;
    private LayoutInflater mInflater;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    };

    public PrivateChatAdapter(Context context) {
        mContext = context;
        mChatEntities = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 添加数据
     */
    public void setDatas(ArrayList<ChatEntity> chatEntities) {
        mChatEntities = chatEntities;
        notifyDataSetChanged();
    }

    public void add(ChatEntity chatEntity) {
        mChatEntities.add(chatEntity);
        notifyDataSetChanged();
    }

    @Override
    public PrivateChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == ITEM_TYPE_COME) {
            itemView = mInflater.inflate(R.layout.private_come, parent, false);
        } else {
            itemView = mInflater.inflate(R.layout.private_self, parent, false);
        }
        return new PrivateChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PrivateChatViewHolder holder, int position) {
        ChatEntity chatEntity = mChatEntities.get(position);
        SpannableString ss = new SpannableString(chatEntity.getMsg());
        holder.mContent.setText(EmojiUtil.parseFaceMsg(mContext, ss));

        //Glide.with(mContext).load(chatEntity.getUserAvatar()).placeholder(R.drawable.chatuser_head_icon).into(holder.mHeadIcon);

        holder.mContent.setOnTouchListener(mTouchListener);
        holder.mHeadIcon.setOnTouchListener(mTouchListener);
    }

    @Override
    public int getItemCount() {
        return mChatEntities.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatEntities.get(position).isPublisher()) {
            return ITEM_TYPE_SELF;
        } else {
            return ITEM_TYPE_COME;
        }
    }

    public final class PrivateChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_private_head)
        public HeadView mHeadIcon;
        @BindView(R.id.id_private_msg)
        TextView mContent;

        PrivateChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
