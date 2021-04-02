package com.example.qydemo0.Widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class QYScrollView  extends ScrollView {


    public interface ScrollViewListener {
        void onScrollChanged(QYScrollView scrollView, int x, int y, int oldx, int oldy);
    }

    private ScrollViewListener scrollViewListener = null;

    public QYScrollView(Context context) {
        super(context);
    }

    public QYScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public QYScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}