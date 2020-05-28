package com.bokecc.sdk.mobile.push.example.listener;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者 ${bokecc}.<br/>
 */

public class ClickItemTouchListener extends RecyclerView.SimpleOnItemTouchListener {

    private OnItemClickListener mOnItemClickListener;
    private GestureDetectorCompat mGestureDetectorCompat;

    public interface OnItemClickListener {
        /**
         * 单击
         */
        void onItemClick(View view, int position);

        /**
         * 长按
         */
        void onItemLongClick(View view, int position);


        void onTouchEventUp();

        void onTouchEventDown();
    }

    public ClickItemTouchListener(final RecyclerView recyclerView,
                                  OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mGestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        View itemView = recyclerView.
                                findChildViewUnder(e.getX(), e.getY());
                        if (itemView != null && mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(itemView,
                                    recyclerView.getChildAdapterPosition(itemView));
                        }
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View itemView = recyclerView.
                                findChildViewUnder(e.getX(), e.getY());
                        if (itemView != null && mOnItemClickListener != null) {
                            mOnItemClickListener.onItemLongClick(itemView,
                                    recyclerView.getChildAdapterPosition(itemView));
                        }
                    }

                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onTouchEventUp();
            }
        } else if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onTouchEventDown();
            }
        }
        mGestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
    }
}
