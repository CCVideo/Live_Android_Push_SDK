package com.bokecc.sdk.mobile.push.example.base.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.base.BasePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class TitleActivity<T extends BasePresenter, V extends TitleActivity.ViewHolder> extends BaseActivity<T> {

    private static final int TITLE_LEFT_FLAG = 0;
    private static final int TITLE_RIGHT_FLAG = 1;

    @BindView(R.id.id_title_content_layout)
    FrameLayout mContentLayout;
    @BindView(R.id.id_title_tool_bar)
    protected Toolbar mTitleBar;
    @BindView(R.id.id_title_text)
    protected TextView mTitleTxt;
    @BindView(R.id.id_title_left_image)
    protected ImageView mTitleLeftImage;
    @BindView(R.id.id_title_right_image)
    protected ImageView mTitleRightImage;

    private View mContentView;
    protected V mViewHolder;
    private OnTitleClickListener mTitleClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_title;
    }

    @Override
    protected void setUpView(Bundle savedInstanceState) {
        mContentLayout.removeAllViews(); // 移除全部的子view
        mContentView = LayoutInflater.from(this).inflate(
                getContentLayoutId(), null
        );
        mContentLayout.addView(mContentView); // 设置当前的内容展示
        mViewHolder = getViewHolder();
    }

    /**
     * 获取主内容布局id
     *
     * @return
     */
    protected abstract int getContentLayoutId();

    protected abstract V getViewHolder();

    /**
     * 得到当前的内容展示区域
     * @return
     */
    protected View getContentView() {
        return mContentView;
    }

    /**
     * 设置标题状态
     *
     * @param leftStatus  左边图片状态
     * @param leftResId   左边图片资源 对应状态为DISSMISS{@link TitleImageStatus} 的时候传递0即可
     * @param title       标题
     * @param rightStatus 右边图片状态
     * @param rightResId  右边图片资源 同左边
     */
    protected void setTitleStatus(TitleImageStatus leftStatus, int leftResId,
                                  String title,
                                  TitleImageStatus rightStatus, int rightResId,
                                  OnTitleClickListener titleClickListener) {
        if (TextUtils.isEmpty(title)) {
            mTitleTxt.setVisibility(View.GONE);
        } else {
            mTitleTxt.setVisibility(View.VISIBLE);
            mTitleTxt.setText(title);
        }
        setTitleImageStatus(TITLE_LEFT_FLAG, leftStatus, leftResId);
        setTitleImageStatus(TITLE_RIGHT_FLAG, rightStatus, rightResId);
        mTitleBar.setTitle(""); // 屏蔽原始的标题
        setSupportActionBar(mTitleBar);
        mTitleClickListener = titleClickListener;
    }

    /**
     * 设置标题左右图片状态
     *
     * @param flag   左右标记
     * @param status 状态
     * @param resId  资源
     */
    private void setTitleImageStatus(int flag, TitleImageStatus status, int resId) {
        ImageView imageView = flag == 0 ? mTitleLeftImage : mTitleRightImage;
        if (status == TitleImageStatus.DISMISS) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(resId);
        }
    }

    @OnClick(R.id.id_title_left_image)
    void leftClickEvent() {
        if (mTitleClickListener != null) {
            mTitleClickListener.onLeftClick();
        }
    }

    @OnClick(R.id.id_title_right_image)
    void rightClickEvent() {
        if (mTitleClickListener != null) {
            mTitleClickListener.onRightClick();
        }
    }

    public enum TitleImageStatus {
        SHOW, DISMISS
    }

    public interface OnTitleClickListener {
        void onLeftClick();

        void onRightClick();
    }

    public static class ViewHolder {
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
