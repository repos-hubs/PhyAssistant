package com.jibo.activity;

import java.io.File;

import com.jibo.GBApplication;
import com.jibo.common.Constant;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ZoomControls;
import com.api.android.GBApp.R;

public class ECGImageActivity extends Activity {
	private ImageView imageView;
	private int position;
	private String id;
	private Bitmap bm;
	private int size;
	private int bmpWidth;
	private int bmpHeight;
	private float scaleWidth = 1;
	private float scaleHeight = 1;
	private ZoomControls zoom;
	private GBApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ecg_pic);
		super.onCreate(savedInstanceState);
		inits();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void inits() {
		app = (GBApplication) getApplication();
		imageView = (ImageView) findViewById(R.id.image);
		position = this.getIntent().getIntExtra("position", 0);
		id = this.getIntent().getStringExtra("id");
		bm = BitmapFactory.decodeFile(Constant.DATA_PATH + "/ecg_img/" + id);
		size = getIntent().getIntExtra("size", 0);
		bmpWidth = bm.getWidth();
		bmpHeight = bm.getHeight();
		Matrix matrix = new Matrix();
		Bitmap resizeBmp = Bitmap.createBitmap(bm, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		if (position <= size - 1) {
			imageView.setImageBitmap(resizeBmp);

			zoom = (ZoomControls) findViewById(R.id.zoomcontrol);
			zoom.setIsZoomInEnabled(true);
			zoom.setIsZoomOutEnabled(true);
			zoom.setOnZoomInClickListener(new OnClickListener() {
				public void onClick(View v) {
					double scale = 1.2;
					scaleWidth = (float) (scaleWidth * scale);
					scaleHeight = (float) (scaleHeight * scale);
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					Bitmap resizeBmp = Bitmap.createBitmap(bm, 0, 0, bmpWidth,
							bmpHeight, matrix, true);
					imageView.setImageBitmap(resizeBmp);
					DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);

					if (resizeBmp.getHeight() >= app.getDeviceInfo().getScreenWidth()
							|| resizeBmp.getWidth() >= app.getDeviceInfo().getScreenHeight()) {
						zoom.setIsZoomInEnabled(false);
						zoom.setIsZoomOutEnabled(true);
					} else {
						zoom.setIsZoomInEnabled(true);
						if(resizeBmp.getHeight() <= (app.getDeviceInfo()
								.getScreenWidth() / 2)
								|| resizeBmp.getWidth() <= (app.getDeviceInfo()
										.getScreenHeight() / 2)) {
							zoom.setIsZoomOutEnabled(false);
						} else {
							zoom.setIsZoomOutEnabled(true);
						}
					}

				}
			});
			zoom.setOnZoomOutClickListener(new OnClickListener() {
				public void onClick(View v) {
					double scale = 0.8;
					scaleWidth = (float) (scaleWidth * scale);
					scaleHeight = (float) (scaleHeight * scale);
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					Bitmap resizeBmp = Bitmap.createBitmap(bm, 0, 0, bmpWidth,
							bmpHeight, matrix, true);
					imageView.setImageBitmap(resizeBmp);
					DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					if (resizeBmp.getHeight() <= (app.getDeviceInfo()
							.getScreenWidth() / 3)
							|| resizeBmp.getWidth() <= (app.getDeviceInfo()
									.getScreenHeight() / 2)) {
						zoom.setIsZoomOutEnabled(false);
						zoom.setIsZoomInEnabled(true);

					} else {

						zoom.setIsZoomOutEnabled(true);
					}
				}

			});
		}
	}
}
