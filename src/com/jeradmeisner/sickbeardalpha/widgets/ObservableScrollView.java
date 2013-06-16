package com.jeradmeisner.sickbeardalpha.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

    private ScrollListener mScrollListener;

    public ObservableScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollListener != null)
            mScrollListener.onScrollChanged(this, l, t, oldl, oldt);
    }

    public int computeVerticalScrollRange()
    {
        return super.computeVerticalScrollRange();
    }

    public void setScrollListener(ScrollListener listener)
    {
        mScrollListener = listener;
    }

    public static interface ScrollListener {
        public void onScrollChanged(View who, int l, int t, int oldl, int oldt);
    }
}