package com.bokecc.sdk.mobile.push.example.popup;

import android.content.Context;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.base.NewBasePopupWindow;
import com.bokecc.sdk.mobile.push.example.base.PopupAnimUtil;
import com.bumptech.glide.Glide;

/**
 * 带文本内容的Loading弹窗
 *
 * @author CC
 */
public class TxtLoadingPopup extends NewBasePopupWindow {

    private ImageView mLoadingIcon;
    private TextView mTip;

    public TxtLoadingPopup(Context context) {
        super(context);
    }

    public TxtLoadingPopup(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    protected void onViewCreated() {
        mLoadingIcon = findViewById(R.id.id_txt_loading_img);
        mTip = findViewById(R.id.id_loading_tip);

        Glide.with(mContext).load(R.drawable.loading).asGif().into(mLoadingIcon);
    }

    public void setTipValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        mTip.setText(value);
    }

    @Override
    protected int getContentView() {
        return R.layout.txt_loading_layout;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefScaleEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefScaleExitAnim();
    }
}
