package com.jibo.activity;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.data.entity.ECGEntity;
import com.jibo.dbhelper.ECGAdapter;
import com.jibo.ui.TextField;
import com.api.android.GBApp.R;
/**
 * 
 * @author gb
 * @description 该Activity为典型异常心电图中某个病症的心电图和症状描述
 *
 */
public class ECGArticleActivity extends BaseSearchActivity{
	private GBApplication app;
	private TextField tvContent;//心电图介绍的内容
	private TextView tvTitle;//症状的名称
	private ImageView imageView;//心电图
	
	private ArrayList<String> fileNameLst;//心电图没有后缀的名称
	private ArrayList<String> allNameLst;//心电图完整的名称
	private int index = -1;//心电图文件在链表中的索引
	private String id;
	private ECGAdapter ecgAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ecg_article);
		super.onCreate(savedInstanceState);
		inits();
	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
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
		ecgAdapter = new ECGAdapter(this, 1);
		tvContent=(TextField) findViewById(R.id.content);
		tvTitle=(TextView) findViewById(R.id.sub_title);
		TextView txtCategory = (TextView) findViewById(R.id.txt_header_title);
		imageView=(ImageView) findViewById(R.id.image);
		imageView.setOnClickListener(new ECGImgClickListener());
		 
		Intent intent = getIntent();
		id = intent.getStringExtra("id");//从上一个Activity钟取得表的ID
		String title = "";
		String content = "";
		if(id == null) {
			int ecgID = intent.getIntExtra("id", -1);
			ECGEntity en = ecgAdapter.getECGByID(""+ecgID);
			title = en.getTitle();
			content = en.getContent();
			id = en.getId();
		} else {
			title = intent.getStringExtra("title");
			content = intent.getStringExtra("content");
		}
		
		tvTitle.setText(title);
		txtCategory.setText(getString(R.string.tool));
		tvContent.setText(content.replaceAll("\\[nn\\]", "\n").replace("\\s*\\n\\s*", "\\n"),this);
		listImage();
		if(id!=null) {
			for(String s:fileNameLst) {
				System.out.println("s   "+s);
				//图片的名称对应于数据表中的ID
				if(s.equals(id)) {
					index  = fileNameLst.indexOf(s);
				}
			}
		}
		Bitmap resizeBmp = null;
		Bitmap bm = null;
		if(index != -1) {
			bm = BitmapFactory.decodeFile(Constant.DATA_PATH+"/ecg_img/"+allNameLst.get(index));//取得该病症对应的图片
	        if(bm == null) return;
			int bmpWidth = bm.getWidth();   //获取当前图片的大小
	        int bmpHeight = bm.getHeight();  
	        Matrix matrix = new Matrix();   
	        matrix.postScale(2, 2);   
	        resizeBmp = Bitmap.createBitmap(bm,0,0,bmpWidth,bmpHeight,matrix,true);//按指定规则处理图片
		}
		
		if (index <= fileNameLst.size() - 1) {
			//如果处理后的照片宽度小于设备的宽度，那就将该文件放上去，否则就放置未处理的照片
			if (resizeBmp!=null && resizeBmp.getWidth() <= app.getDeviceInfo().getScreenWidth()) {
				imageView.setImageBitmap(resizeBmp);
			} else {
				imageView.setImageBitmap(bm);
			}
		}
	}
	
	private class ECGImgClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();  
			intent.setClass(ECGArticleActivity.this, ECGImageActivity.class);
	        intent.putExtra("position", index);
	        intent.putExtra("id", allNameLst.get(index));
	        intent.putExtra("size", allNameLst.size());
			startActivity(intent);  
		}
	}
	
	/**
	 * @功能：取得心电图图片的完整名称链表和没有后缀的名称链表
	 * 
	 */
	public void listImage() {
		File file = new File(Constant.DATA_PATH+"/ecg_img");
		fileNameLst = new ArrayList<String>();//只有个文件的名称没有后缀名
		allNameLst = new ArrayList<String>();//文件名的全称包括后缀名
		if(file.exists()) {
			if(file.isDirectory()) {
				File[] fileArr = file.listFiles();
				for(File f:fileArr) {
					String name = f.getName();
					fileNameLst.add(name.substring(0, name.lastIndexOf(".")));
					allNameLst.add(name);
				}
			}
		}
	}

}
