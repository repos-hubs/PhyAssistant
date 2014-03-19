package com.jibo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

public class ScrollViewGallery extends Gallery {

    private static final int DRAG_BOUNDS_IN_DP = 20;

    private static final int SCROLL_LOCK_NONE = 0;
    private static final int SCROLL_LOCK_VERTICAL = 1;
    private static final int SCROLL_LOCK_HORIZONTAL = 2;

    private int mDragBoundsInPx = 0;

    private float mTouchStartX;
    private float mTouchStartY;

    private int mScrollLock = SCROLL_LOCK_NONE;

    public ScrollViewGallery(Context context) {
        super(context);
        initCustomGallery(context);
    }

    public ScrollViewGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCustomGallery(context);
    }

    public ScrollViewGallery(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        initCustomGallery(context);
    }

    private void initCustomGallery(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        mDragBoundsInPx = (int) (scale*DRAG_BOUNDS_IN_DP + 0.5f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mTouchStartX = ev.getX();
            mTouchStartY = ev.getY();
            mScrollLock = SCROLL_LOCK_NONE;

            super.onTouchEvent(ev);
            break;

        case MotionEvent.ACTION_MOVE:
            if (mScrollLock == SCROLL_LOCK_VERTICAL) {
                return false;
            }

            final float touchDistanceX = (ev.getX() - mTouchStartX);
            final float touchDistanceY = (ev.getY() - mTouchStartY);

            if (Math.abs(touchDistanceY) > mDragBoundsInPx) {
                mScrollLock = SCROLL_LOCK_VERTICAL;
                return false;
            }
            if (Math.abs(touchDistanceX) > mDragBoundsInPx) {
                mScrollLock = SCROLL_LOCK_HORIZONTAL; // gallery action
                return true; // redirect MotionEvents to ourself
            }
            break;

        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            super.onTouchEvent(ev);
            break;
        }

        return false;
    }
}