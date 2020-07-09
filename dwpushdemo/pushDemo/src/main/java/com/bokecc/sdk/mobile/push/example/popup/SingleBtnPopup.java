package com.bokecc.sdk.mobile.push.example.popup;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.widget.Button;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.base.BasePopupWindow;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dds on 2020/6/17.
 */
public class SingleBtnPopup extends BasePopupWindow {

    @BindView(R.id.id_choose_dialog_tip)
    TextView mTip;
    @BindView(R.id.id_choose_dialog_ok)
    Button mOk;

    private SingleBtnPopup.OnOkClickListener mOnOkClickListener;

    /**
     * @param context 上下文
     */
    public SingleBtnPopup(Context context) {
        super(context);
    }

    @Override
    protected int getContentView() {
        return R.layout.single_btn_layout;
    }

    /**
     * 设置提示
     *
     * @param value 提示
     */
    public void setTip(String value) {
        setTip(value, null, -1, -1);
    }

    public void setTip(String value, CharacterStyle cStyle, int start, int end) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        if (cStyle == null || start == -1 || end == -1 || start >= end) {
            mTip.setText(value);
            return;
        }
        SpannableString ss = new SpannableString(value);
        ss.setSpan(cStyle, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTip.setText(ss);
    }

    /**
     * 设置确定按钮显示
     *
     * @param value 显示文字
     */
    public void setOKText(String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        mOk.setText(value);
    }


    /**
     * 设置确定按钮键听
     *
     * @param onOkClickListener {@link CommonPopup}
     */
    public void setOnOkClickListener(SingleBtnPopup.OnOkClickListener onOkClickListener) {
        mOnOkClickListener = onOkClickListener;
    }

    @OnClick(R.id.id_choose_dialog_ok)
    void ok() {
        if (mOnOkClickListener == null) {
            return;
        }
        if (isFastClick()) return;
        mOnOkClickListener.onOk();
    }

    public interface OnOkClickListener {
        void onOk();
    }

}
