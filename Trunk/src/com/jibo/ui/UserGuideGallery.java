package com.jibo.ui;

import com.jibo.activity.UserGuideActivity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class UserGuideGallery extends Gallery {
	private int position = 1;
	
	public UserGuideGallery(Context context) {
		super(context);
	}
	
	public UserGuideGallery(Context context,AttributeSet attrs) {
		super(context, attrs);
	}
	
	public UserGuideGallery(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int kEvent;
		if(position < UserGuideActivity.COUNT){
			if(e2.getX() > e1.getX()){ //Check if scrolling left
		          kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		          position--;
		        }else{ //Otherwise scrolling right
		          kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		          position++;
		        }
		        onKeyDown(kEvent, null);
		}else{
			UserGuideActivity.doCallback();
		}
        
        return true;  
	}
	
}