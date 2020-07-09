package com.bokecc.sdk.mobile.push.example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.util.DensityUtil;

import java.util.ArrayList;

/**
 * 作者 ${bokecc}.<br/>
 */
public class SingleChooseHorizontalLayout extends LinearLayout {

    private static final String TAG = SingleChooseHorizontalLayout.class.getSimpleName();

    private Context mContext;
    private ArrayList<SelectEffect> mEffects;
    private OnItemSelectListener mSelectListener;

    private LinearLayout itemRootLayout;
    private int mDividLineHeight;
    private int mDividLineColor = Color.parseColor("#5ba9cf");

    public SingleChooseHorizontalLayout(Context context) {
        this(context, null);
    }

    public SingleChooseHorizontalLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleChooseHorizontalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOrientation(VERTICAL);

        mDividLineHeight = DensityUtil.dp2px(mContext, 0.5f);
        TypedArray mArray = context.obtainStyledAttributes(attrs,
                R.styleable.SingleChooseHorizontalLayout, defStyleAttr, 0);
        for (int i = 0; i < mArray.getIndexCount(); i++) {
            int index = mArray.getIndex(i);
            if (index == R.styleable.SingleChooseHorizontalLayout_dividLineColor) {
                mDividLineColor = mArray.getColor(index, Color.parseColor("#5ba9cf"));
            } else if (index == R.styleable.SingleChooseHorizontalLayout_dividLineHeight) {
                mDividLineHeight = mArray.getDimensionPixelOffset(index, 1);
            }
        }
        mArray.recycle();

        addDividLine();
        addItemRootLayout();
    }

    /**
     * 添加模块item根布局
     */
    private void addItemRootLayout() {
        itemRootLayout = new LinearLayout(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        itemRootLayout.setLayoutParams(params);
        itemRootLayout.setOrientation(HORIZONTAL);
        addView(itemRootLayout);
    }

    /**
     * 添加分割线
     */
    private void addDividLine() {
        View view = new View(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mDividLineHeight);
        view.setLayoutParams(params);
        view.setBackgroundColor(mDividLineColor);
        addView(view);
    }

    /**
     * 设置每一个子view的背景
     *
     * @param effects 资源集合
     */
    public void setItemBackground(ArrayList<SelectEffect> effects) {
        if (null == effects || effects.isEmpty()) {
            return;
        }
        mEffects = effects;
        for (int i = 0; i < effects.size(); i++) {
            LinearLayout itemLayout = new LinearLayout(mContext);
            itemLayout.setOrientation(VERTICAL);
            LayoutParams layoutParams = new LayoutParams(0,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            layoutParams.gravity = Gravity.CENTER;
            itemLayout.setLayoutParams(layoutParams);
            itemLayout.setClickable(true);
            itemLayout.setOnClickListener(new ItemClickListener(i));
            itemRootLayout.addView(itemLayout);
            ImageView itemIcon = new ImageView(mContext);
            if (i == 0)
                itemIcon.setBackgroundResource(effects.get(i).mSelectedResId);
            else
                itemIcon.setBackgroundResource(effects.get(i).mNormalResId);
            LayoutParams iconParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            iconParams.gravity = Gravity.CENTER;
            itemIcon.setLayoutParams(iconParams);
            itemLayout.addView(itemIcon);
        }
    }

    /**
     * 清空item的状态
     */
    private void clearItemStatus() {
        for (int i = 0; i < itemRootLayout.getChildCount(); i++) {
            ImageView imageView = getItemImageView(i);
            imageView.setBackgroundResource(mEffects.get(i).mNormalResId);
        }
    }

    /**
     * 得到当前位置item上面的ImageView
     * @param position 位置
     * @return ImageView
     */
    private ImageView getItemImageView(int position) {
        return (ImageView) ((LinearLayout) itemRootLayout.
                getChildAt(position)).getChildAt(0);
    }

    /**
     * 设置默认选中的位置
     * @param position 位置
     */
    public void setDefaultSelected(int position) {
        if (itemRootLayout.getChildCount() <= 0) {
            return;
        }
        if (position < 0 || position > itemRootLayout.getChildCount()) {
            return;
        }
        clearItemStatus();
        ImageView iconView = getItemImageView(position);
        iconView.setBackgroundResource(mEffects.get(position).mSelectedResId);
    }

    public void setOnItemSelectListener(OnItemSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    private class ItemClickListener implements OnClickListener {

        private int mPosition;

        public ItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            clearItemStatus();
            ImageView iconView = getItemImageView(mPosition);
            iconView.setBackgroundResource(mEffects.get(mPosition).mSelectedResId);
            if (mSelectListener != null) {
                mSelectListener.onItemSelected(mPosition);
            }
        }
    }

    public static final class SelectEffect {
        int mNormalResId;
        int mSelectedResId;

        public SelectEffect(int normalResId, int selectedResId) {
            mNormalResId = normalResId;
            mSelectedResId = selectedResId;
        }

    }

    public interface OnItemSelectListener {
        /**
         * item被选中的回调
         *
         * @param position 位置
         */
        void onItemSelected(int position);
    }

}
