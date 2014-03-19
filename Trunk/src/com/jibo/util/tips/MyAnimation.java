package com.jibo.util.tips;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class MyAnimation {
	private static Animation animation = null;
	
	public static Animation load_Animation(Context context, int the_Animation_ID) {

		if (the_Animation_ID != 0) {
			animation = AnimationUtils.loadAnimation(context, the_Animation_ID);
			animation.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		return animation;
	}
}
