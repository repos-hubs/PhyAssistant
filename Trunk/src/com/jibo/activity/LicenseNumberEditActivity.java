package com.jibo.activity;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.common.BitmapManager;
import com.jibo.common.Util;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;

/**
 * 修改执业证号，执业证号拍照
 * 
 * @author simon
 * 
 */
public class LicenseNumberEditActivity extends BaseActivity implements
		OnClickListener {

	/** UI */
	private EditText licNoInput;
	private Button submitBtn;
	private RelativeLayout takePhotoLay;
	private ImageView takePhoto;
	private TextView status;
	private TextView explainLink;

	private Context context;
	
	private Bitmap bitmap;
	private String base64String = "";
	private String contentType ="";
	private final int getDrugPhotoFromLocal = 1;
	private final int getDrugPhotoFromCamera = 2;
	private String photoPath ="";
	private String photoPathSaved;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.licensenumber_edit);
		context = this;
		Intent intent = getIntent();
		String licenumber = intent.getStringExtra("licenumber");
		String statusString = intent.getStringExtra("status");
		photoPathSaved = intent.getStringExtra("photoPath");
		String checkInfo = intent.getStringExtra("checkInfo");
		licNoInput = (EditText) findViewById(R.id._edittext);
		submitBtn = (Button) findViewById(R.id.register_submit);
		takePhotoLay = (RelativeLayout) findViewById(R.id.take_photo_lay);
		takePhoto = (ImageView) findViewById(R.id.take_photo);
		status = (TextView) findViewById(R.id.status_text);
		explainLink = (TextView) findViewById(R.id.take_photo_explain_link);
		explainLink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		explainLink.getPaint().setAntiAlias(true);
		if (statusString!="" && statusString.equals("200")) {			
			status.setText(checkInfo);
			makeNotClickable(licNoInput);
			makeNotClickable(takePhotoLay);
		} else {
			status.setText(checkInfo);
		}
		try{
			if(!TextUtils.isEmpty(photoPathSaved)){
				//照片框中中显示图片
				setBitmap(getBitmapByFile(photoPathSaved));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		///
		licNoInput.setText(licenumber);
		licNoInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				licNoInput.removeTextChangedListener(this);
				//makeNotClickable(takePhotoLay);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		submitBtn.setOnClickListener(this);
		takePhotoLay.setOnClickListener(this);
		explainLink.setOnClickListener(this);
		MobclickAgent.onError(this);
	}

	private void makeNotClickable(View view) {
		view.setClickable(false);
		view.setFocusable(false);
		view.setLongClickable(false);
		view.setEnabled(false);
	}

	@Override
	public void onClick(View v) {
		if (!v.isEnabled()) {
			Log.e("simon", "on click error, button is disabled");
			return;
		}
		Log.i("id",v.getId()+"");
		switch (v.getId()) {
		case R.id.register_submit: // 确定
			try{
			Intent intent = new Intent();
			if (takePhotoLay.isEnabled()) {
//				intent.putExtra("base64String", base64String);
//				intent.putExtra("contentType", contentType);
				if(!TextUtils.isEmpty(photoPath)){
					intent.putExtra("photoPath", photoPath);
				}else if(!TextUtils.isEmpty(photoPathSaved)){
					intent.putExtra("photoPath", photoPathSaved);
					
				}
					
				intent.putExtra("licenseNumber", licNoInput.getText()
						.toString());
			}
			setResult(RESULT_OK, intent);
			finish();
			}catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.take_photo_lay: // 拍照或取照片
			new PictureMethodPopupWindow(context).showAtLocation(
					LicenseNumberEditActivity.this.findViewById(R.id.mai),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.take_photo_explain_link://用户隐私声明
			startActivity(new Intent(this,TextViewActivity.class).putExtra(TextViewActivity.TITLE, getString(R.string.take_photo_explain_title)).putExtra(TextViewActivity.CONTENT, getString(R.string.take_photo_explain)).putExtra("titleIcon",-1));
			break;
		}
	}
	
	public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
		 final float densityMultiplier = context.getResources().getDisplayMetrics().density;        
		 int h= (int) (newHeight*densityMultiplier);
		 int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
		 photo=Bitmap.createScaledBitmap(photo, w, h, true);
		 return photo;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case getDrugPhotoFromLocal:
				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, null, null,
						null, null);
				cursor.moveToFirst();
				// String imgNo = cursor.getString(0); // 图片编号
				 String imgPath1 = cursor.getString(1); // 图片文件路径
				// String imgSize = cursor.getString(2); // 图片大小
				String imgName = cursor.getString(3); // 图片文件名
				Logs.i("imgPath1"+imgPath1);
				Logs.i("imgName"+imgName);
				// String fileName = imgName;
				 String fileSize = cursor.getString(4);
				ContentResolver cr = this.getContentResolver();
				try {

//					InputStream in = cr.openInputStream(uri);
//					setBitmap(BitmapFactory.decodeStream(in));
					setBitmap(getBitmapByFile(imgPath1));
//					in = cr.openInputStream(uri);
//					base64String = Util.getBase64(in);//加密图片流
//					contentType = imgName.substring(imgName.indexOf(".") + 1);
					photoPath = imgPath1;
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case getDrugPhotoFromCamera:
//				Bundle extras = data.getExtras();
//				Bitmap bm = (Bitmap) extras.get("data");
				Bitmap bm = getBitmapByFile(photoPath);
				if (bm == null)
					return;
//				
//				//Bitmap newBm = scaleDownBitmap(bm, 50, context);
				setBitmap(bm);
//				BitmapManager.saveBmpToSd(bitmap, imageUrl);
//				String imageUrl = UUID.randomUUID().toString() + ".jpg";
//				photoPath = "/data"
//						+ Environment.getDataDirectory().getAbsolutePath()
//						+ "/com.api.android.GBApp/image"+"/"+imageUrl;
//				InputStream in = BitmapManager.getLocalInputStream(imageUrl);
//				base64String = Util.getBase64(in);
//				contentType = "jpg";
				break;
			default:
				break;
			}
		}
	}
	
	private Bitmap getBitmapByFile(String photoPath){
	    Bitmap b = null;
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        File f = new File(photoPath);
	        FileInputStream fis = new FileInputStream(f);
	        BitmapFactory.decodeStream(fis, null, o);
	        fis.close();
	        int scale = 10;
//	        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
//	            scale = Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
//	        }
//	        int scale = (int)(o.outHeight/200);
//	        if(scale<=0){
//	        	scale = 10;
//	        }
	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        fis = new FileInputStream(f);
	        b = BitmapFactory.decodeStream(fis, null, o2);
	        fis.close();
	    } catch (FileNotFoundException e) {
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return b;
	}
	private void setBitmap(Bitmap map) {
		if (map != null){
			if(bitmap != null)
				bitmap.recycle();
			if(takePhotoLay.getWidth()>0&&takePhotoLay.getHeight()>0){
				bitmap = Util.zoomImage(map, takePhotoLay.getWidth(), takePhotoLay.getHeight()); 
			}else{
				bitmap = Util.zoomImage(map, 120,120);
			}
		}else{
			return;
		}
		
		/* 将Bitmap设定到ImageView */
		takePhotoLay.setBackgroundDrawable(new BitmapDrawable(bitmap));
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) takePhoto
				.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		takePhoto.setLayoutParams(params);
	}

	public class PictureMethodPopupWindow extends PopupWindow {

		private Button btnSelectPicture, btnCamera, btn_cancel;
		private View mMenuView;

		public PictureMethodPopupWindow(Context context) {
			super(context);

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mMenuView = inflater.inflate(R.layout.picture_select_popupwindow,
					null);

			btnSelectPicture = (Button) mMenuView
					.findViewById(R.id.select_picture);
			// 设置按钮监听
			btnSelectPicture.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//makeNotClickable(licNoInput);
					Intent intent = new Intent();
					intent.setType("image/*");
					// intent.setType("audio/*"); //选择音频
					// intent.setType("video/*"); //选择视频 （mp4 3gp
					// 是android支持的视频格式）
					// intent.setType("video/*;image/*");//同时选择视频和图片
					/* 使用Intent.ACTION_GET_CONTENT这个Action */
					intent.setAction(Intent.ACTION_GET_CONTENT);
					/* 取得相片后返回本画面 */
					LicenseNumberEditActivity.this.startActivityForResult(
							intent, getDrugPhotoFromLocal);
					dismiss();
				}
			});

			btnCamera = (Button) mMenuView.findViewById(R.id.camera);
			btnCamera.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//makeNotClickable(licNoInput);
//					Intent intent = new Intent(
//							"android.media.action.IMAGE_CAPTURE");

					String imageUrl = UUID.randomUUID().toString() + ".jpg";
					photoPath = Environment.getExternalStorageDirectory().getAbsolutePath()
							+"/"+imageUrl;
					Logs.i(photoPath);
					
					//必须确保文件夹路径存在，否则拍照后无法完成回调
					File vFile = new File(photoPath);
					if (vFile.exists())
						vFile.delete();
					try {
						vFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					Uri uri = Uri.fromFile(vFile);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
					///
					LicenseNumberEditActivity.this.startActivityForResult(
							intent, getDrugPhotoFromCamera);
					dismiss();
				}
			});

			btn_cancel = (Button) mMenuView.findViewById(R.id.cancel);
			// 取消按钮
			btn_cancel.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// 销毁弹出框
					dismiss();
				}
			});

			// 设置SelectPicPopupWindow的View
			this.setContentView(mMenuView);
			// 设置SelectPicPopupWindow弹出窗体的宽
			this.setWidth(LayoutParams.FILL_PARENT);
			// 设置SelectPicPopupWindow弹出窗体的高
			this.setHeight(LayoutParams.WRAP_CONTENT);
			// 设置SelectPicPopupWindow弹出窗体可点击
			this.setFocusable(true);
			// 设置SelectPicPopupWindow弹出窗体动画效果
			// this.setAnimationStyle(R.style.AnimBottom);
			// 实例化一个ColorDrawable颜色为半透明
			ColorDrawable dw = new ColorDrawable(0x00000000);
			// 设置SelectPicPopupWindow弹出窗体的背景
			this.setBackgroundDrawable(dw);
			// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
			mMenuView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {

					int height = mMenuView.findViewById(R.id.pop_layout)
							.getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							dismiss();
						}
					}
					return true;
				}
			});

		}
	}

}
