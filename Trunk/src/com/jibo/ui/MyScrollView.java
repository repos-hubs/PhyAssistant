package com.jibo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * 解决当ScrollView包含listView或者多个焦点时，会自动滚动的问题。
 * 
 * 记录滚动点，重新scrollTo
 *
 */
public class MyScrollView extends ScrollView{


	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	float x = 0;
    float y = 0;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int intx = (int)x;
        int inty = (int)y;
        super.onLayout(changed, l, t, r, b);
        this.scrollTo(intx, inty);  
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        x = this.getScrollX();
        y = this.getScrollY();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
	
	
}
