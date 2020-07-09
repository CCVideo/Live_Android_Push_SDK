package com.bokecc.sdk.mobile.push.example.view;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者 ${bokecc}.<br/>
 */
public class ItemLayout extends FrameLayout {

    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int BOTH = 2;

    public static final int ITEM_VISIBLE = 0;
    public static final int ITEM_GONE = 1;

    private Context mContext;

    private View mItemView;
    private View mTopLineView, mBottomLineView;
    private TextView mTipView, mValueView;
    private ImageView mIconView;

    @IntDef({TOP, BOTTOM, BOTH})
    @Retention(RetentionPolicy.SOURCE) //注解保留范围为源代码
    public @interface LineMode {
    }

    @IntDef({ITEM_VISIBLE, ITEM_GONE})
    @Retention(RetentionPolicy.SOURCE) //注解保留范围为源代码
    public @interface ItemViewStatus {
    }

    public ItemLayout(Context context) {
        this(context, null);
    }

    public ItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        initItemMainLayout();
    }

    /**
     * 初始化主要布局
     */
    private void initItemMainLayout() {
        mItemView = LayoutInflater.from(mContext).
                inflate(R.layout.push_setting_item_layout, null);
        mTopLineView = findById(R.id.id_item_top_line);
        mBottomLineView = findById(R.id.id_item_bottom_line);
        mTipView = (TextView) findById(R.id.id_item_tip);
        mValueView = (TextView) findById(R.id.id_item_value);
        mIconView = (ImageView) findById(R.id.id_item_icon);
        addView(mItemView);
        setClickable(true);
    }

    /**
     * 根据id查找子view
     *
     * @param id 被查找子view的id
     * @return View
     */
    private View findById(int id) {
        return mItemView.findViewById(id);
    }

    /**
     * 设置分割线显示的模式
     *
     * @param lineMode 分割线的模式 {@link ItemLayout} <ul><li>TOP</li><li>BOTTOM</li><li>BOTH</li></ul>
     */
    public void setDivideLineMode(@LineMode int lineMode) {
        if (lineMode == TOP) {
            mTopLineView.setVisibility(VISIBLE);
            mBottomLineView.setVisibility(GONE);
        } else if (lineMode == BOTTOM) {
            mTopLineView.setVisibility(GONE);
            mBottomLineView.setVisibility(VISIBLE);
        } else if (lineMode == BOTH) {
            mTopLineView.setVisibility(VISIBLE);
            mBottomLineView.setVisibility(VISIBLE);
        }
    }

    /**
     * 设置item内容状态
     *
     * @param iconStatus  图标状态
     * @param resId       图标资源id 不显示图标的时候建议传递0
     * @param tipStatus   提示状态
     * @param tipValue    提示文字内容 不显示的时候建议传递null
     * @param valueStatus 值状态
     * @param defValue    值默认文字内容 同提示
     */
    public void setItemContentStatus(@ItemViewStatus int iconStatus, int resId,
                                     @ItemViewStatus int tipStatus, String tipValue,
                                     @ItemViewStatus int valueStatus, String defValue) {
        setItemViewStatus(iconStatus, mIconView);
        setItemViewStatus(tipStatus, mTipView);
        setItemViewStatus(valueStatus, mValueView);
        if (iconStatus == ITEM_VISIBLE) {
            mIconView.setBackgroundResource(resId);
        }
        if (tipStatus == ITEM_VISIBLE) {
            if (!TextUtils.isEmpty(tipValue))
                mTipView.setText(tipValue);
        }
        if (valueStatus == ITEM_VISIBLE) {
            if (!TextUtils.isEmpty(defValue))
                mValueView.setText(defValue);
        }
    }

    /**
     * 设置子view的状态
     *
     * @param status   状态
     * @param itemView 子view
     */
    private void setItemViewStatus(@ItemViewStatus int status, View itemView) {
        if (status == ITEM_VISIBLE) {
            itemView.setVisibility(VISIBLE);
        } else if (status == ITEM_GONE) {
            itemView.setVisibility(GONE);
        }
    }

    /**
     * 设置item提示
     *
     * @param value 提示
     */
    public void setTip(String value) {
        if (!TextUtils.isEmpty(value)) {
            mTipView.setVisibility(VISIBLE);
            mTipView.setText(value);
        }
    }

    /**
     * 设置item当前属性值
     *
     * @param value 值
     */
    public void setValue(String value) {
        if (!TextUtils.isEmpty(value)) {
            mValueView.setVisibility(VISIBLE);
            mValueView.setText(value);
        }
    }

    /**
     * 设置item图标
     *
     * @param resId 图标资源id
     */
    public void setIcon(int resId) {
        mIconView.setVisibility(VISIBLE);
        mIconView.setBackgroundResource(resId);
    }

    /**
     * 设置提示文字颜色
     *
     * @param color 颜色
     */
    public void setTipTxtColor(int color) {
        mTipView.setTextColor(color);
    }

    /**
     * 设置值文字颜色
     *
     * @param color 颜色
     */
    public void setValueTxtColor(int color) {
        mValueView.setTextColor(color);
    }

    /**
     * 设置提示文字尺寸
     *
     * @param size 尺寸 单位sp
     */
    public void setTipTxtSize(int size) {
        mTipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * 设置值文字尺寸
     *
     * @param size 尺寸 单位sp
     */
    public void setValueTxtSize(int size) {
        mValueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

}
