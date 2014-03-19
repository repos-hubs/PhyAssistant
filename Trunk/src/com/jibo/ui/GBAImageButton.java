package com.jibo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GBAImageButton extends RelativeLayout {
	private ImageView mImage;
	private TextView mText;

	public GBAImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		int fc = LayoutParams.FILL_PARENT;
		int wc = LayoutParams.FILL_PARENT;

		RelativeLayout.LayoutParams params = new LayoutParams(fc, wc);
		setLayoutParams(params);

		RelativeLayout.LayoutParams params1 = new LayoutParams(fc, wc);
		mText = new TextView(context, attrs);
		//params1.leftMargin = 10;
		//params1.topMargin  = 5;
		params1.rightMargin = 30;
		params1.addRule(RelativeLayout.CENTER_VERTICAL);
		params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//		params1.addRule(RelativeLayout.LEFT_OF,1);   将控件放在指定id的左边
		mText.setLayoutParams(params1);
		addView(mText);

		RelativeLayout.LayoutParams params2 = new LayoutParams(fc, wc);
		mImage = new ImageView(context, attrs);
		
		if(getResolution(context) <= (320*480))
			params2.leftMargin = 260;
		else
			params2.leftMargin  = 400;
		
		//params2.topMargin   = 15;
		//params2.rightMargin = 10;
		params2.addRule(RelativeLayout.CENTER_VERTICAL);
		params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mImage.setLayoutParams(params2);
		addView(mImage);

		setClickable(true);
		setFocusable(true);
		//setBackgroundResource(R.drawable.gba_stretching_frame);
	}
	
	private int getResolution(Context context){
		int Width  = 0;
		int Height = 0;
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Width  = manager.getDefaultDisplay().getWidth();
		Height = manager.getDefaultDisplay().getHeight();
		return Width * Height;
	}
	

	public void GBAImageButtonSetText(String string) {
		mText.setText(string);
	}
	
	public void GBAImagesetBackgroundResource(int resid) {
		setBackgroundResource(resid);
	}
	
	public void GBAArrowImagesetResource(int resId){
		mImage.setImageResource(resId);
	}
}
