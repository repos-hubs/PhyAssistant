/**
 * 
 */
package com.jibo.activity;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.ui.HomePageLayout;
import com.jibo.ui.UserGuideGallery;

/**
 * @author Administrator
 *
 */
public class UserGuideActivity extends Activity {
	private UserGuideGallery gallery;
	public static final int COUNT = 6;
	private static Activity context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		gallery = new UserGuideGallery(this);
		setContentView(gallery);
		context = this;
		String filePath = Constant.DATA_PATH + File.separator + "welcome";
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			Intent intent = new Intent(this, HomePageActivity.class);
			HomePageLayout.s_pageID = 0;
			startActivity(intent);
		}

		String[] paths = new String[6];

		for (int i = 0; i < 6; i++) {
			if (i < 9)
				paths[i] = "gba0" + String.valueOf(i + 1) + ".jpg";
			else
				paths[i] = "gba" + String.valueOf(i + 1) + ".jpg";
		}
		ImageAdapter imageAdapter = new ImageAdapter(this, paths);
		gallery.setAdapter(imageAdapter);
	}
	
	public static void doCallback(){
//		Intent intent = new Intent(context, Registration_000Activity.class);
//		context.startActivity(intent);
//		context.finish();
	}
	
	public static void deleteImages(){
		String filePath = Constant.DATA_PATH + File.separator + "welcome";
		File file = new File(filePath);
		if(file != null && file.exists())
			file.delete();
	}
	
	private class ImageAdapter extends BaseAdapter{
		private Context context;
		private String[] imagePaths;
		private ImageView tempView;
		private Bitmap bitmap ;
		
		public ImageAdapter(Context context, String[] imagePaths) {
			super();
			this.context = context;
			this.imagePaths = imagePaths;
		}

		@Override
		public int getCount() {
			return imagePaths.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				tempView = new ImageView(context);
			}else{
				tempView = (ImageView)convertView;
			}
			
			bitmap = BitmapFactory.decodeFile(Constant.DATA_PATH + File.separator + "welcome" + File.separator + imagePaths[position]);
			tempView.setImageBitmap(bitmap);
			return tempView;
		}
		
	}
	
}
