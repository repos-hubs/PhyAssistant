package com.jibo.ui;


import com.jibo.activity.HomePageActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class CircleIndicator extends SurfaceView implements Callback, Runnable {
	SurfaceHolder holder;
	Canvas mCanvas;
	Paint mPaint;
	private float radius;
	float cx = 0;
	float centeringOffset = 0;
	Thread th;
	private static final int STYLE_STROKE = 0;
	private static final int STYLE_FILL = 1;
	private final Paint mPaintActive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mPaintInactive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int childCount;
	private float scale;
	private Context mContext;
	public CircleIndicator(Context context, int childCount, float scale) {
		super(context);
		mContext = context;
		holder = getHolder();
//		holder.setFormat(PixelFormat.TRANSPARENT);
//		setZOrderOnTop(true);
		holder.addCallback(this);
        
		mPaint = new Paint();
		this.scale = scale;
		if(this.scale == 1.0) {
			radius = 6;
		} else {
			radius = 10;
		}
		this.childCount = childCount;
		initColors(Color.GREEN, Color.GREEN, STYLE_FILL, STYLE_STROKE);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	public void surfaceCreated(SurfaceHolder holder) {
		th = new Thread(this);
		th.start();
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	public void run() {
		draw();
	}
	public void draw() {
		float circleSeparation = 2*radius+radius;
		float centeringOffset = 0;
		int leftPadding = 1;
		float x = 0;
		float y = 0;
		mCanvas = holder.lockCanvas();
		try {
			if(mContext instanceof HomePageActivity) {
				mPaintActive.setColor(Color.rgb(51, 89, 126));
				mPaintInactive.setColor(Color.rgb(51, 89, 126));
				mCanvas.drawColor(Color.rgb(188, 226, 244));
				for (int iLoop = 0; iLoop < childCount; iLoop++) {
					x = leftPadding + radius + (iLoop * circleSeparation) + centeringOffset;
					y = getPaddingTop() + radius;
					mCanvas.drawCircle(x,y, radius, mPaintInactive);
				}
				if(HomePageLayout.s_pageID == 0) {
					mCanvas.drawCircle(leftPadding + radius + centeringOffset,y, radius, mPaintActive);
				} else {
					mCanvas.drawCircle(x,y, radius, mPaintActive);
				}
				
			} else {
				mCanvas.drawColor(Color.rgb(0, 85, 125));
				for (int iLoop = 0; iLoop < childCount; iLoop++) {
					x = leftPadding + radius + (iLoop * circleSeparation) + centeringOffset;
					y = getPaddingTop() + radius;
					mCanvas.drawCircle(x,y, radius, mPaintInactive);
				}
				mCanvas.drawCircle(x,y, radius, mPaintActive);
			}
		} catch(Exception e) {
			Log.v("Indicator", "is error");
		} finally {
			holder.unlockCanvasAndPost(mCanvas);
		}
	}
	private void initColors(int activeColor, int inactiveColor, int activeType,
			int inactiveType) {
		// Select the paint type given the type attr
		switch (inactiveType) {
		case STYLE_FILL:
			mPaintInactive.setStyle(Style.FILL);
			break;
		default:
			mPaintInactive.setStyle(Style.STROKE);
		}
		mPaintInactive.setColor(inactiveColor);

		// Select the paint type given the type attr
		switch (activeType) {
		case STYLE_STROKE:
			mPaintActive.setStyle(Style.STROKE);
			break;
		default:
			mPaintActive.setStyle(Style.FILL);
		}
		mPaintActive.setColor(activeColor);
	}
}

