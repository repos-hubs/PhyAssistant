package com.jibo.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import com.api.android.GBApp.R;

public class BitmapText extends BitmapDrawable { 
    private String name; 
    private Bitmap bitmap; 
    private Bitmap background;
    private int bmWidth;
    private int bmHeight;
    private Context mContext;
    private int screenWidth;
	private int w;
	private int h;
    
    public  BitmapText(Context ctx,String strName,int bitmapId,Bitmap background, int w, int h) { 
            super(BitmapFactory.decodeResource(ctx.getResources(), bitmapId)); 
            this.w = w;
            this.h = h;
            
            bitmap =getBitmap(); 
            this.background = background;
            name = strName; 
            this.mContext = ctx;
            screenWidth=((Activity)ctx).getWindowManager().getDefaultDisplay().getWidth();
            bmWidth = bitmap.getWidth();
            bmHeight = bitmap.getHeight();
            this.setBounds(0, 0, bmWidth, bmHeight); 
    } 
    @Override 
	public void draw(Canvas arg0) {
		Paint textPaint = new Paint();
		arg0.drawBitmap(bitmap, w-bmWidth, this.getBounds().top,
				textPaint);
		textPaint.setTextSize((float) (bmWidth*0.18));
		textPaint.setTypeface(Typeface.DEFAULT);
		textPaint.setAntiAlias(true);
		textPaint.setStyle(Style.FILL);
		if (name.equals("0")) {

		} else {
			int x = 0;
			int y = bmHeight/5;
//			x = (int) (bmWidth*0.62);
			
			textPaint.setColor(Color.YELLOW);
			RectF rf = new RectF(x, 0, bmWidth, bmHeight*3/10);
//			arg0.drawBitmap(background, x, 0, textPaint);
			FontMetrics fm = textPaint.getFontMetrics();
			float[] widths = new float[1];
			String srt = String.valueOf("8");
			textPaint.getTextWidths(srt, widths);
			switch(name.length()) {
			case 1:
				x = (int) (0.7*bmWidth);
				break;
			case 2:
				x = (int) (0.67*bmWidth);
				break;
			case 3:
				x = (int) (bmWidth*0.62);
				break;
			}
			arg0.drawText(name, (float)((bmWidth+x)/2-Math.ceil(widths[0])*name.length()/2), y, textPaint);
		}
	}
    

} 