package com.jibo.ui;

import com.api.android.GBApp.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RippleView extends RelativeLayout{
	@SuppressWarnings("unused")
	private Context context;
	private LayoutInflater layoutInflater;
	private ImageView rippleImageView, handImageView;
	private AnimationDrawable animationDrawable=new AnimationDrawable();
	
	private static final int TRANSLATE_P1_X = -50;
	private static final int TRANSLATE_P2_X = 100;
	
	
	public RippleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		layoutInflater.inflate(R.layout.ripple, this, true);
		inits();
		slideview(TRANSLATE_P1_X, TRANSLATE_P2_X);
	}
	
	private void inits(){
		rippleImageView = (ImageView) findViewById(R.id.rippleImage);
		handImageView = (ImageView) findViewById(R.id.handImage);
	}
	
	public void slideview(final float p1, final float p2) {
		for(int i=0;i<6;i++)
		{
			animationDrawable.addFrame(this.getResources().getDrawable(R.drawable.light+i), 400);
		}
		animationDrawable.setOneShot(false);
		rippleImageView.setBackgroundDrawable(animationDrawable);
		rippleImageView.getViewTreeObserver().addOnPreDrawListener(opdl); 
		TranslateAnimation animation = new TranslateAnimation(p1, p2, 0, 0);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(2400);
		animation.setRepeatMode(Animation.REVERSE);
		animation.setRepeatCount(-1);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		rippleImageView.startAnimation(animation);
		handImageView.startAnimation(animation);
	 }
	
	OnPreDrawListener opdl=new OnPreDrawListener(){  
	      @Override  
	      public boolean onPreDraw() {  
	    	 animationDrawable.start();  
	         return true;
	      }  
	   };  

}
