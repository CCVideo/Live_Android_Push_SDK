package com.bokecc.sdk.mobile.push.example.util;

import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 作者 ${bokecc}.<br/>
 */
public class TextViewUtil {
    private TextViewUtil() {
    }

    /**
     * 修改EditText hint 文字显示大小
     * @param size 尺寸 单位sp
     * @param editText EditText
     */
    public static void changeEditTextHintSize(int size, EditText editText) {
        String hintTxt = editText.getHint().toString().trim();
        if (TextUtils.isEmpty(hintTxt)) {
            return;
        }
        SpannableString spannalbeStr = new SpannableString(hintTxt);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(size, true);
        spannalbeStr.setSpan(absoluteSizeSpan, 0, hintTxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(spannalbeStr);
    }

    /**
     * 获取TextView内容的宽度
     *
     * @param textView TextView
     * @return int
     */
    public static float getTextViewContentWidth(TextView textView) {
        if (textView == null) {
            throw new NullPointerException("参数不存在");
        }
        Paint paint = textView.getPaint();
        String content = textView.getText().toString();
        return paint.measureText(content);
    }

}
