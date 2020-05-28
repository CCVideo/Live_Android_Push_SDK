package com.bokecc.sdk.mobile.push.example.popup;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.base.BasePopupWindow;
import com.bokecc.sdk.mobile.push.example.base.adapter.SingleChooseAdapter;
import com.bokecc.sdk.mobile.push.example.base.adapter.SingleViewHolder;

import butterknife.BindView;

/**
 * 作者 ${bokecc}.<br/>
 */
public class SingleChoosePopup extends BasePopupWindow {

    @BindView(R.id.id_single_choose_title)
    TextView mTitle;
    @BindView(R.id.id_single_choose_group)
    RecyclerView mGroup;

    /**
     * 点击回调
     */
    private OnItemClickListener mItemClickListener;

    /**
     * 点击位置
     */
    private int mSelectedPosition = -1;

    /**
     * @param context 上下文
     */
    public SingleChoosePopup(Context context) {
        super(context);
        configRecycleViewItemClickEvent();
    }

    public SingleChoosePopup(Context context, int width) {
        super(context, width);
        configRecycleViewItemClickEvent();
    }

    public void setAdapter(SingleChooseAdapter adapter) {
        mGroup.setAdapter(adapter);
    }

    /**
     * 配置RecycleView item 点击事件
     */
    private void configRecycleViewItemClickEvent() {
        final GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(
                mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View itemView = mGroup.findChildViewUnder(e.getX(),
                        e.getY());
                if (itemView != null) {
                    int position = (int) itemView.getTag();
                    if (mItemClickListener != null) {
                        if (isDismissing) {
                            return true;
                        }
                        View preItemView = getItemViewAt(mSelectedPosition);
                        View curItemView = getItemViewAt(position);
                        if (preItemView != null && curItemView != null) {
                            if (mGroup.getChildViewHolder(preItemView) instanceof SingleViewHolder
                                    && mGroup.getChildViewHolder(curItemView) instanceof SingleViewHolder) {
                                SingleViewHolder preHolder = (SingleViewHolder) mGroup.getChildViewHolder(preItemView);
                                SingleViewHolder curHolder = (SingleViewHolder) mGroup.getChildViewHolder(curItemView);
                                mItemClickListener.onItemClick(itemView, mSelectedPosition, position, preHolder, curHolder);
                            }
                        }

                        mSelectedPosition = position;
                    }
                }
                return true;
            }
        });
        mGroup.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetectorCompat.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetectorCompat.onTouchEvent(e);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        mGroup.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    protected int getContentView() {
        return R.layout.single_choose_layout;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        mTitle.setText(title);
    }

    /**
     * 设置默认选择的位置
     *
     * @param selectedPosition 位置
     */
    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
    }

    /**
     * 获取选择的位置
     *
     * @return 位置
     */
    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    /**
     * 获取指定位置的itemview
     *
     * @param position 位置
     * @return itemiew
     */
    private View getItemViewAt(int position) {
        if (position < 0 || position >= mGroup.getChildCount()) {
            return null;
        }
        return mGroup.getChildAt(position);
    }

    /**
     * 设置选项点击回调事件
     *
     * @param itemClickListener {@link OnItemClickListener}
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * 点击选项的回调事件
         *
         * @param itemView    被点击的item
         * @param prePosition 前一次被点击的item的位置
         * @param curPosition 被点击的item的位置
         */
        void onItemClick(View itemView, int prePosition, int curPosition, SingleViewHolder preHolder, SingleViewHolder curHolder);
    }

}
