package com.kky.wangfang.stickyheadrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 实现锚定顶部的recyclerView Title
 */
public class StickHeadItemDecoration extends RecyclerView.ItemDecoration {
    private View mView;

    private View mPreView;
    private int mLastIndex = -1;

    private boolean isPullUp = false;
    private boolean isInitLayout = false;
    private final int[] mHeader = {0,2,5,6,8,12,14,15,16,20};
    private final String[] headerStr = {"item0","item2","item5","item6","item8","item12","item14","item15","item16","item20"};
    private int mHeight;

    StickHeadItemDecoration(Context context, int layout,int height) {
        mView = LayoutInflater.from(context).inflate(layout, null);
        mHeight = height;
    }

    private int findHead(int position) {
        for (int i = 0; i < mHeader.length; i++) {
            if (position == mHeader[i])
                return i;
        }
        return -1;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (findHead(parent.getChildAdapterPosition(view)) != -1) {
            outRect.set(0, mHeight, 0, 0);
        } else {
            super.getItemOffsets(outRect,view,parent,state);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (!isInitLayout) {
            int width = parent.getMeasuredWidth();

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.UNSPECIFIED);
            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec, 0, width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,0, mHeight);

            mView.measure(childWidth, childHeight);
            mView.layout(0, 0, width, mHeight);
            mView.measure(widthSpec,heightSpec);

            isInitLayout = true;
        }
        //具体逻辑如下：

        //mPreView 记录上一个View，这个View为RecyclerView中上一个具有title的item，此item
        //  一直显示在顶端。向上拉时，该View实现向上隐藏的挤压动画。当向下拉时，该View实现向下
        //  显示的动画。
        //offset 是动画实现最重要的变量，用于计算挤压动画时，view的偏移值
        //mLastIndex 用于记录title显示内容的索引
        int count = parent.getChildCount();

        int offset = 0;

        if (parent.getChildAdapterPosition(parent.getChildAt(0)) < mHeader[0]) {
            mLastIndex = -1;
        }

        for (int i = 0; i < count; i++) {
            final View child = parent.getChildAt(i);
            int itemTop = child.getTop();
            int position = parent.getChildAdapterPosition(child);
            int itemPosition = findHead(position);
            if (itemPosition != -1) {

                int left = child.getLeft();

                //当当前控件消失时，则设置为pre控件
                if (itemTop - mHeight <= 0) {
                    mPreView = child;
                    mLastIndex = itemPosition;
                    isPullUp = false;
                }

                //向下拉的情况
                if (child == mPreView && itemTop > mHeight && itemTop < 2 * mHeight) {
                    offset = itemTop - 2 * mHeight;
                    isPullUp = true;
                }

                //向上推的情况，挤压动画
                if (child != mPreView && itemTop - 2 * mHeight <= 0) {
                    offset = itemTop - 2 * mHeight;
                    isPullUp = false;
                }

                c.save();
                c.translate(left, itemTop - mHeight);

                //绘制除pre之外的分割
                TextView textView = (TextView) mView.findViewById(R.id.text);
                textView.setText(headerStr[itemPosition]);
                mView.draw(c);
                c.restore();

            }
        }
        //绘制pre的分割
        if (mPreView != null) {
            if ((isPullUp && mLastIndex == 0)) {
                return;
            }
            c.save();
            c.translate(mPreView.getLeft(), offset);
            TextView textView = (TextView) mView.findViewById(R.id.text);
            textView.setText(headerStr[isPullUp ? mLastIndex - 1: mLastIndex]);
            mView.draw(c);
            c.restore();
        }
    }

}
