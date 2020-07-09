package com.bokecc.sdk.mobile.push.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.model.PrivateUser;
import com.bokecc.sdk.mobile.push.example.util.EmojiUtil;
import com.bokecc.sdk.mobile.push.example.view.HeadView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 私聊用户适配器
 *
 * @author CC
 */
public class PrivateUserAdapter extends RecyclerView.Adapter<PrivateUserAdapter.PrivateUserViewHolder> {

    private Context mContext;
    private ArrayList<PrivateUser> mPrivateUsers;
    private LayoutInflater mInflater;

    public PrivateUserAdapter(Context context) {
        mPrivateUsers = new ArrayList<>();
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 添加数据
     */
    public void add(PrivateUser privateUser) {
        int index = -1;
        for (PrivateUser user : mPrivateUsers) {
            if (user.getId().equals(privateUser.getId())) {
                index = mPrivateUsers.indexOf(user);
                break;
            }
        }
        if (index != -1) {
            mPrivateUsers.remove(index);
        }
        mPrivateUsers.add(0, privateUser);
        notifyDataSetChanged();
    }

    public ArrayList<PrivateUser> getPrivateUsers() {
        return mPrivateUsers;
    }

    @Override
    public PrivateUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PrivateUserViewHolder(mInflater.inflate(R.layout.private_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PrivateUserViewHolder holder, int position) {
        PrivateUser privateUser = mPrivateUsers.get(position);
        holder.mUserName.setText(privateUser.getName());
        SpannableString ss = new SpannableString(privateUser.getMsg());
        holder.mContent.setText(EmojiUtil.parseFaceMsg(mContext, ss));
        holder.mTime.setText(privateUser.getTime());
        Glide.with(mContext).load(privateUser.getAvatar()).placeholder(R.drawable.chatuser_head_icon).into(holder.mHeadIcon);
        if (privateUser.isRead()) {
            holder.mHeadIcon.clearNew();
        } else {
            holder.mHeadIcon.updateNew();
        }
    }

    @Override
    public int getItemCount() {
        return mPrivateUsers.size();
    }

    final class PrivateUserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_private_user_head)
        HeadView mHeadIcon;
        @BindView(R.id.id_private_time)
        TextView mTime;
        @BindView(R.id.id_private_user_name)
        TextView mUserName;
        @BindView(R.id.id_private_msg)
        TextView mContent;

        PrivateUserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
