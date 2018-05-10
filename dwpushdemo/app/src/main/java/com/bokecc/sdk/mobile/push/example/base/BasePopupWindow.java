package com.bokecc.sdk.mobile.push.example.base;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.util.DensityUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者 ${郭鹏飞}.<br/>
 */
public abstract class BasePopupWindow implements View.OnClickListener {

    protected Context mContext;

    private View mPopContentView;
    private View mOutsideView, mAnimView;
    private PopupWindow mPopupWindow;
    private boolean isOutsideCancel = false;
    protected boolean isDismissing = false; // 正在dismiss

    private Unbinder mUnbinder;

    private OnPopupWindowDismissListener mDismissListener;

    /**
     * @param context 上下文
     */
    public BasePopupWindow(Context context) {
        this(context, 250);
    }

    /**
     * @param context 上下文
     * @param width   可见区域的宽度
     */
    public BasePopupWindow(Context context, int width) {
        mContext = context;
        mPopContentView = LayoutInflater.from(mContext).
                inflate(getContentView(), null);
        mOutsideView = findById(R.id.id_popup_window_outside_view);
        mAnimView = findById(R.id.id_popup_window_anim_view);
        // 适配横竖屏
        ViewGroup.LayoutParams params = mAnimView.getLayoutParams();
        params.width = DensityUtil.dp2px(mContext, width);
        mAnimView.setLayoutParams(params);
        mOutsideView.setClickable(true);
        mOutsideView.setOnClickListener(this);
        mAnimView.setOnClickListener(this); // 主要作用是拦截内容区域点击事件
        mPopupWindow = new PopupWindow(mPopContentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        configPopupWindow();
        mUnbinder = ButterKnife.bind(this, mPopContentView);
    }

    /**
     * 配置PopupWindow
     */
    private void configPopupWindow() {
        //指定透明背景，back键相关 默认响应返回键
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setFocusable(true);
        // 点击空白区域
        mPopupWindow.setOutsideTouchable(true);
        //无需动画
        mPopupWindow.setAnimationStyle(0);
        // 添加dismiss监听
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mDismissListener != null) {
                    mDismissListener.onDismiss();
                }
            }
        });
    }

    /**
     * 设置点击popupwindow外部是否可以取消
     *
     * @param flag 布尔类型
     */
    public void setOutsideCancel(boolean flag) {
        isOutsideCancel = flag;
    }

    /**
     * 设置popupwindow外部背景颜色
     *
     * @param color 颜色值
     */
    public void setOutsideBackgroundColor(int color) {
        mOutsideView.setBackgroundColor(color);
    }

    public void setBackPressedCancel(boolean flag) {
        if (flag) {
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        } else {
            mPopupWindow.setBackgroundDrawable(null);
        }
    }

    /**
     * 获取子view
     *
     * @param resId 子viweid
     * @return View
     */
    public View findById(int resId) {
        return mPopContentView.findViewById(resId);
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    /**
     * 显示
     */
    public void show(View view) {
        if (isShowing()) {
            return;
        }
        mPopupWindow.showAtLocation(view,
                Gravity.CENTER, 0, 0);
        mAnimView.startAnimation(getEnterAnimation());
        Animation animation = getEnterAnimation();
        animation.setAnimationListener(new PopupAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                onStartAnimFinish();
            }
        });
        mAnimView.startAnimation(animation);
    }

    /**
     * 开始动画结束回调
     */
    protected void onStartAnimFinish() {
        // Ingore 交由子类需求
    }

    protected Animation getEnterAnimation() {
        return getDefaultScaleAnimation();
    }

    protected Animation getExitAnimation() {
        return getScaleAnimation();
    }

    protected Animation getDefaultScaleAnimation() {
        Animation scaleAnimation =
                new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }

    protected Animation getScaleAnimation() {
        Animation scaleAnimation =
                new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }

    /**
     * 隐藏
     *
     * @param dismissStatusListener 状态回调
     */
    public void dismiss(final OnDismissStatusListener dismissStatusListener) {
        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                if (dismissStatusListener != null) {
                    dismissStatusListener.onDismissStart();
                }
                Animation animation = getExitAnimation();
                animation.setAnimationListener(new PopupAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mPopupWindow.dismiss();
                        isDismissing = false;
                        if (dismissStatusListener != null) {
                            dismissStatusListener.onDismissEnd();
                        }
                    }
                });
                mAnimView.startAnimation(animation);
            }
        }
    }

    /**
     * 释放
     */
    public void release() {
        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            mPopupWindow = null;
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /**
     * 设置dismiss监听回调
     *
     * @param dismissListener
     */
    public void setOnPopupWindowDismissListener(
            OnPopupWindowDismissListener dismissListener) {
        mDismissListener = dismissListener;
    }

    protected abstract int getContentView();

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_popup_window_outside_view) {
            if (isOutsideCancel) {
                if (!isDismissing) {
                    isDismissing = true;
                    dismiss(null);
                }
            }
        }
    }

    public interface OnPopupWindowDismissListener {
        void onDismiss();
    }

    private abstract class PopupAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public interface OnDismissStatusListener {
        /**
         * 消失前调用
         */
        void onDismissStart();

        /**
         * 消失后调用
         */
        void onDismissEnd();
    }

}
