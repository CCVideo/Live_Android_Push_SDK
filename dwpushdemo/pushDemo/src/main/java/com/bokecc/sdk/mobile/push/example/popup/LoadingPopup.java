package com.bokecc.sdk.mobile.push.example.popup;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.base.BasePopupWindow;
import com.bokecc.sdk.mobile.push.example.view.DotsTextView;

import butterknife.BindView;

/**
 * 作者 ${bokecc}.<br/>
 */
public class LoadingPopup extends BasePopupWindow {

    @BindView(R.id.id_loading_tip)
    TextView mTip;
    @BindView(R.id.id_loading_dots)
    DotsTextView mDots;

    /**
     * @param context 上下文
     */
    public LoadingPopup(Context context) {
        super(context);
        mDots.hideAndStop(null);
    }

    @Override
    protected int getContentView() {
        return R.layout.loading_layout;
    }

    @Override
    protected void onStartAnimFinish() {
        mDots.showAndPlay();
    }

    @Override
    public void dismiss(final OnDismissStatusListener statusListener) {
        mDots.hideAndStop(new DotsTextView.OnAnimEndListener() {
            @Override
            public void onEnd() {
                LoadingPopup.super.dismiss(statusListener);
            }
        });
    }

    /**
     * 设置加载文字
     *
     * @param value 文字
     */
    public void setTipValue(String value) {
        if (!TextUtils.isEmpty(value)) {
            mTip.setText(value);
        }
    }

    /**
     * 设置文字尺寸 单位sp
     *
     * @param size 大小
     */
    public void setTipSize(int size) {
        mTip.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * 设置文字颜色
     *
     * @param color 颜色
     */
    public void setTipColor(int color) {
        mTip.setTextColor(color);
    }

}
