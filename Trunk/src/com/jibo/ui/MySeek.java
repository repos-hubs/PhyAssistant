package com.jibo.ui;

import com.api.android.GBApp.R;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MySeek extends RelativeLayout implements ViewFactory {
	private int maxbar;
	private int minbar;
	private int nowProgress;
	private final SeekBar seek;
	private final View view ;
	private Context context;
	public MySeek(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		view = LayoutInflater.from(getContext()).inflate(R.layout.seekbar_layout, this);
		seek = ((SeekBar)this.findViewById(R.id.seekbar));
		setOnSeekBarChangeListener();
		TextView textView = (TextView)view.findViewById(R.id.seekBarText);
		textView.setText(getContext().getString(R.string.filter_if)+0+"");
	}
	public int getMaxbar() {
		return maxbar;
	}

	public void setMaxbar(int maxbar) {
		this.maxbar = maxbar;
	}

	public int getMinbar() {
		return minbar;
	}

	public void setMinbar(int minbar) {
		this.minbar = minbar;
	}
	public void setProgress(int progress){
		seek.setProgress(progress);
	}
	public int getProgress(){
		return seek.getProgress();
	}

	
	public void setOnSeekBarChangeListener(){
		try{
//			seek.setThumb(getThumb());
			seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					MobclickAgent.onEvent(context, "research_latest_filter_if");
				}			
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}			
				int newProgress;
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					Log.i("progressMax", seek.getMax()+"");
					double meige = (double)seek.getMax()/16;
					double yushu = progress % meige;
					newProgress = progress;
					Log.i("progressMax", progress+"");
					if(yushu > meige/2){
						newProgress = (int) Math.ceil(progress);
					}else if(yushu < meige/2){
						newProgress = (int) Math.floor(progress);
					}
					Log.i("progress", newProgress+"");
					seek.setProgress(newProgress);
					nowProgress = newProgress;
					String blank = "";
					if(newProgress<10){
						blank = " ";
					}
					TextView textView = (TextView)view.findViewById(R.id.seekBarText);
					textView.setText(seekBar.getContext().getString(R.string.filter_if)+newProgress+blank);
				}
			}
			);
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	private View myThumb;
	private Drawable getThumb() {
//		LayoutInflater inflater = LayoutInflater.from(this);
//		myThumb = inflater.inflate(R.layout.thumb, null);
		myThumb = View.inflate(getContext(), R.layout.thumb, null);
		myThumb.setDrawingCacheEnabled(true);
		Bitmap bitmap = myThumb.getDrawingCache();
		//Drawable drawable1 = getResources().getDrawable(R.drawable.triangle);
		Drawable drawable1 = layoutToDrawable(R.layout.thumb);
		return drawable1;
	}
	public Drawable layoutToDrawable(int layout_id){
		View viewHelp = View.inflate(getContext(), layout_id, null);
//		LayoutInflater inflator = getLayoutInflater();
//		LayoutInflater inflator = null;
//	     View viewHelp = inflator.inflate(layout_id, null);
	     
//	     TextView textView = (TextView)viewHelp.findViewById(R.id.seekBarNum);
//	     textView.setText("15");
		 if(false == viewHelp.isDrawingCacheEnabled())
			viewHelp.setDrawingCacheEnabled(true);
		 viewHelp.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
			      MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		 viewHelp.layout(0, 0, viewHelp.getMeasuredWidth(), viewHelp.getMeasuredHeight());
		 viewHelp.buildDrawingCache();
	     Bitmap bitmap = viewHelp.getDrawingCache();
	     Drawable drawable = (Drawable)new BitmapDrawable(bitmap);
	     ///
	     ImageView imageView = (ImageView) viewHelp.findViewById(R.id.imview);
	     /* 将Bitmap设定到ImageView */
	     imageView.setImageBitmap(bitmap);
	     
	     ///
	     return drawable;
	}
	public static Bitmap drawableToBitmap(Drawable drawable) {          
        Bitmap bitmap = Bitmap  
                        .createBitmap(  
                                        drawable.getIntrinsicWidth(),  
                                        drawable.getIntrinsicHeight(),  
                                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                                        : Bitmap.Config.RGB_565);  
        Canvas canvas = new Canvas(bitmap);  
        //canvas.setBitmap(bitmap);  
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());  
        drawable.draw(canvas);  
        return bitmap;  
}
}
