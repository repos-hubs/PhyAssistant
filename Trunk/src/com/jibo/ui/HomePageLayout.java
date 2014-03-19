package com.jibo.ui;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.jibo.activity.HomePageActivity;
import com.jibo.util.tips.TipHelper;
public class HomePageLayout extends ViewGroup {    
    private static final String TAG = "ScrollLayout";    
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;    
        
    private int mCurScreen;    
    private int mDefaultScreen = 0;    
        
    private static final int TOUCH_STATE_REST = 0;    
    private static final int TOUCH_STATE_SCROLLING = 1;    
        
    private static int SNAP_VELOCITY;    
        
    private int mTouchState = TOUCH_STATE_REST;    
    private int mTouchSlop;    
    private float mLastMotionX;    
    private Context mContext;
    
    public static int s_pageID;
    public HomePageLayout(Context context, AttributeSet attrs) {    
        this(context, attrs, 0);    
        mContext = context;
        SNAP_VELOCITY = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
    }
    
    public HomePageLayout(Context context) {    
        super(context);    
        mContext = context;
        SNAP_VELOCITY = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
    }    
    
    public HomePageLayout(Context context, AttributeSet attrs, int defStyle) {    
        super(context, attrs, defStyle);    
        mScroller = new Scroller(context);    
        SNAP_VELOCITY = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth(); 
        mCurScreen = mDefaultScreen;    
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();    
    }    
    @Override    
    protected void onLayout(boolean changed, int l, int t, int r, int b) {    
        if (changed) {    
            int childLeft = 0;    
            final int childCount = getChildCount();    
                
            for (int i=0; i<childCount; i++) {    
                final View childView = getChildAt(i);    
                if (childView.getVisibility() != View.GONE) {    
                    final int childWidth = childView.getMeasuredWidth();    
                    childView.layout(childLeft, 0,     
                            childLeft+childWidth, childView.getMeasuredHeight());    
                    childLeft += childWidth;    
                }    
            }    
        }    
    }    
    @Override      
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {       
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);       
      
        final int width = MeasureSpec.getSize(widthMeasureSpec);       
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);       
        if (widthMode != MeasureSpec.EXACTLY) {       
            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!");     
        }       
      
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);       
        if (heightMode != MeasureSpec.EXACTLY) {       
            throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");    
        }       
      
        final int count = getChildCount();       
        for (int i = 0; i < count; i++) {       
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);       
        }       
        scrollTo(mCurScreen * width, 0);             
    }      
        
    public void snapToDestination() {    
        final int screenWidth = getWidth();    
        final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth;  
        snapToScreen(destScreen);    
        s_pageID = mCurScreen;
    	((HomePageActivity) mContext).drawCircle();

    }    
        
    public void snapToScreen(int whichScreen) {    
        // get the valid layout page    
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));    
        if (getScrollX() != (whichScreen*getWidth())) {    
                
            final int delta = whichScreen*getWidth()-getScrollX();    
            mScroller.startScroll(getScrollX(), 0,     
                    delta, 0, Math.abs(delta)*2);   
            System.out.println("mCurScreen   "+mCurScreen);
            mCurScreen = whichScreen;    
            invalidate();       // Redraw the layout    
        }    
    }    
        
    public void setToScreen(int whichScreen) {    
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));    
        mCurScreen = whichScreen;    
        scrollTo(whichScreen*getWidth(), 0);    
    }    
        
    public int getCurScreen() {    
        return mCurScreen;    
    }    
        
    @Override    
    public void computeScroll() {    
        if (mScroller.computeScrollOffset()) {    
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());    
            postInvalidate();    
        }    
    }    
    @Override    
    public boolean onTouchEvent(MotionEvent event) {        	
        if (mVelocityTracker == null) {    
            mVelocityTracker = VelocityTracker.obtain();    
        }    
        mVelocityTracker.addMovement(event);    
            
        final int action = event.getAction();    
        final float x = event.getX();    
        final float y = event.getY();    
            
        switch (action) {    
        case MotionEvent.ACTION_DOWN:    
            Log.e(TAG, "event down!");    
            if (!mScroller.isFinished()){    
                mScroller.abortAnimation();    
            }    
            mLastMotionX = x;    
            break;    
                
        case MotionEvent.ACTION_MOVE:    
            int deltaX = (int)(mLastMotionX - x); 
            if((deltaX<0 && mCurScreen == 0) || (deltaX>0 && mCurScreen == getChildCount()-1)){
				return false;
			}
            mLastMotionX = x;    
                
            scrollBy(deltaX, 0);    
            break;    
                
        case MotionEvent.ACTION_UP:    
            Log.e(TAG, "event : up");       
            // if (mTouchState == TOUCH_STATE_SCROLLING) {       
            final VelocityTracker velocityTracker = mVelocityTracker;       
            velocityTracker.computeCurrentVelocity(1000);       
            int velocityX = (int) velocityTracker.getXVelocity();       
            Log.e(TAG, "velocityX:"+velocityX);     
                
            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {       
                // Fling enough to move left       
                Log.e(TAG, "snap left");    
                snapToScreen(mCurScreen - 1);     
                s_pageID--;
            	((HomePageActivity) mContext).drawCircle();
            } else if (velocityX < -SNAP_VELOCITY       
                    && mCurScreen < getChildCount() - 1) {       
                // Fling enough to move right       
                Log.e(TAG, "snap right");    
                snapToScreen(mCurScreen + 1);       
                s_pageID++;
            	((HomePageActivity) mContext).drawCircle();
            } else {    
            	System.out.println("aaaaaaaa");
                snapToDestination();       
            }       
            if (mVelocityTracker != null) {       
                mVelocityTracker.recycle();       
                mVelocityTracker = null;       
            }       
            // }       
            mTouchState = TOUCH_STATE_REST;       
            break;    
        case MotionEvent.ACTION_CANCEL:    
            mTouchState = TOUCH_STATE_REST;    
            break;    
        }    
            
        return true;    
    }    
    @Override    
    public boolean onInterceptTouchEvent(MotionEvent ev) {    
        final int action = ev.getAction();    
        if ((action == MotionEvent.ACTION_MOVE) &&     
                (mTouchState != TOUCH_STATE_REST)) {    
            return true;    
        }    
            
        final float x = ev.getX();    
        final float y = ev.getY();    
            
        switch (action) {    
        case MotionEvent.ACTION_MOVE:    
            final int xDiff = (int)Math.abs(mLastMotionX-x);    
            if (xDiff>mTouchSlop) {    
                mTouchState = TOUCH_STATE_SCROLLING;    
            }    
            break;    
                
        case MotionEvent.ACTION_DOWN:    
            mLastMotionX = x;    
            mTouchState = mScroller.isFinished()? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;    
            break;    
                
        case MotionEvent.ACTION_CANCEL:    
        case MotionEvent.ACTION_UP:    
            mTouchState = TOUCH_STATE_REST;    
            break;    
        }    
            
        return mTouchState != TOUCH_STATE_REST;    
    }    
        
}    