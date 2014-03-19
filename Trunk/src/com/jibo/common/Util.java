package com.jibo.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.asynctask.DownloaderResume;
import com.jibo.base.src.request.impl.db.SqliteAdapterCentre;
import com.jibo.dao.DBFactory;
import com.jibo.dao.DBHelper;
import com.jibo.data.entity.AshfEntity;
import com.jibo.entity.ManufutureBrandInfo;
import com.jibo.util.Base64New;
import com.jibo.util.Logs;
/**
 * @author peter.pan
 */

public class Util {
	// 存放各个下载器
	public static Map<String, DownloaderResume> downloaders = new HashMap<String, DownloaderResume>();
	// 存放与下载器对应的进度条
	public static Map<String, ProgressBar> ProgressBars = new HashMap<String, ProgressBar>();

	public static boolean isUrl(String strUrl) {
		String strPattern = "[a-zA-z]+://[^\\s]*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strUrl);
		return m.matches();
	}

	public static final String sp_pic_blank = "        a";
	public static boolean isFirstLoadHomepage = true;
	/**
	 * @author Rafeal Piao
	 * @Description Set Spannel TextView's style
	 * @param Context
	 *            context, TextView txt, String str
	 * @return void
	 */
	public static void createSpaString(Context context, TextView txt, String str) {
		if (txt != null) {
			float scale = context.getResources().getDisplayMetrics().density;
			txt.setTextColor(context.getResources().getColor(R.color.title));
			txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			txt.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
			txt.getPaint().setFakeBoldText(true);

			Drawable drawable = context.getResources().getDrawable(
					R.drawable.sp_arrow);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
			SpannableString spannable = new SpannableString(str + sp_pic_blank);
			spannable.setSpan(span, str.length() + sp_pic_blank.length() - 1,
					str.length() + Util.sp_pic_blank.length(),
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			txt.setText(spannable);
		}
	}

	private static Map<String, ManufutureBrandInfo> manufutureBrandMap = Collections
			.synchronizedMap(new WeakHashMap<String, ManufutureBrandInfo>());

	public static LRULinkedHashMap<String, ArrayList<AshfEntity>> ahfsMap = new LRULinkedHashMap<String, ArrayList<AshfEntity>>();

	/**
	 * 获取解析后的厂商信息
	 * 
	 * @param entity
	 * @return
	 */
	public static ManufutureBrandInfo getPaserObj(String brandId) {
		return manufutureBrandMap.get(brandId);
	}

	public static void setPaserObj(ManufutureBrandInfo entity) {
		manufutureBrandMap.put(entity.getBrandId(), entity);
	}

	// public static String[] filesNames = new String[] { "interaction.db",
	// "hospital.db", "specuse.db", "searchdrug.db",
	// "product_basic_info_local.db", "drugitems.db", "tumor.db",
	// "approval_info.db", "medschool.db", "specialty.db", "department.db" };

	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		if (file != null && file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	// public static boolean isFilesExist() {
	// boolean result = false;
	// File file;
	// String path = Constant.DATA_PATH + File.separator + "GBADATA";
	// if (filesNames.length < 1) {
	// result = false;
	// } else {
	// for (int i = 0; i < filesNames.length; i++) {
	// file = new File(path + File.separator + filesNames[i]);
	// if (file == null || !file.exists()) {
	// result = true;
	// break;
	// }
	// }
	// }
	//
	// return result;
	// }

	/**
	 * @author Rafeal Piao
	 * @Description Remove Duplicate in the list
	 * @param ArrayList
	 * @return void
	 */
	public static void removeDuplicate(ArrayList arlList) {
		HashSet h = new HashSet(arlList);
		h.addAll(arlList);
		arlList.clear();
		arlList.addAll(h);
	}

	public static String getSystemTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		return formatter.format(date);
	}

	public static void setTextProperties(Activity mActivity,
			LinearLayout layout, String text, int x, int y) {
		int Width = 0;
		int Height = 0;
		Width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
		Height = mActivity.getWindowManager().getDefaultDisplay().getHeight();

		TextView textView = new TextView(mActivity);
		textView.setText(text);

		if ((Width * Height) <= (320 * 480)) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
		} else {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25);
		}

		textView.setPadding(x, y, 0, 0);
		// textView.setTypeface(null, Typeface.BOLD);
		textView.setTextColor(mActivity.getResources().getColor(R.color.black));

		layout.addView(textView);
	}

	static long time;
	static long curr;

	public static void printTime(String tag) {
		if (time == 0)
			time = System.currentTimeMillis();
		curr = System.currentTimeMillis();
		System.out.println(tag + (curr - time));
		time = curr;
	}

	/**
	 * 检查db
	 * 
	 * @param daoSession
	 * @return
	 */
	public static boolean checkData(Context context) {
		// db文件不存在
		Logs.i("--- !isDBFileExist() " + !isDBFileExist());
		if (!isDBFileExist())
			return false;

		DBHelper helper = DBFactory.getSDCardDbHelper(context);
		Cursor cur = helper
				.getSQLiteDatabase()
				.rawQuery(
						"select count(*) from sqlite_master where type ='table' and name = 'version'",
						null);
		boolean isVersionTableExist = false;
		if (cur != null) {
			cur.moveToNext();
			isVersionTableExist = cur.getInt(0) > 0;
		}
		// version表不存在
		Logs.i("--- !isVersionTableExist " + !isVersionTableExist);
		if (!isVersionTableExist)
			return false;
		String dbVersion = "";
		SqliteAdapterCentre.getInstance().renew(DBFactory.SDCard_DB_NAME);
		Cursor cursor = SqliteAdapterCentre.getInstance().get(DBFactory.SDCard_DB_NAME).getCursor("select version_code from version",null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			dbVersion = cursor.getString(0);
		}
		// 检查当前DB版本和应用对应DB版本是否一致
		Logs.i("--- " + dbVersion + " " + Constant.dbVersion);
		if (TextUtils.isEmpty(dbVersion)
				|| !dbVersion.equals(Constant.dbVersion))
			return false;
		return true;
	}

	/***
	 * db文件是否存在
	 * 
	 * @return
	 */
	public static boolean isDBFileExist() {
		return isFileExist(DBFactory.SDCard_DB_NAME);
	}

	/**
	 * 获取当前应用程序版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return verName;
	}

	/**
	 * 获取当前应用程序版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String errorHandle2_0_2(String curVerName) {
		if(curVerName.equals("2.02")){
			curVerName = "2.01";
		}else if(curVerName.equals("2.1.1")){
			curVerName = "2.1";
		}else if(curVerName.equals("2.1.3")){
			curVerName = "2.1";
		}
		return curVerName;
	}

	/**
	 * @author Rafeal Piao
	 * @description 删除文件夹或文件
	 * @param path
	 */
	public static void deleteDir(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				File fileList[] = file.listFiles();
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					for (File f : fileList) {
						if (f.isDirectory()) {
							deleteDir(f.getAbsolutePath());
						} else {
							f.delete();
						}
					}
				}
				file.delete();
			} else {
				file.delete();
			}
		}
	}

	/**
	 * 检查email格式
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		String strPattern = "^[a-zA-Z0-9](([\\w\\.-]+)*[a-zA-Z0-9])*@[a-zA-Z0-9]([\\w|-]*[a-zA-Z0-9])*(\\.[a-zA-Z]{2,4}){1,2}$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/**
	 * 检查电话号码格式
	 * 
	 * @param numberString
	 * @return
	 */
	public static boolean isPhoneNumber(String numberString) {
		String pattern = "(^(\\d{3,4})?\\d{7,8})$|(1[0-9]{10})|([0-9]{7,8})";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(numberString);
		return m.matches();
	}

	/***
	 * 检测当前语言环境是否为中文环境
	 * 
	 * @return
	 */
	public static boolean isZh() {
		return Locale.getDefault().getLanguage().contains("zh");
	}

	public static void bindAlertAlarm(Context context) {
		Calendar c = Calendar.getInstance();
		AlarmManager mAlarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent mAlarmIntent = new Intent(Constant.alarmIntentAction);
		PendingIntent mAlarmPendingIntent = PendingIntent.getBroadcast(context,
				0, mAlarmIntent, 0);
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				c.getTimeInMillis(), Constant.intervalTimeMills,
				mAlarmPendingIntent);
		Log.i("simon", "设置闹钟");
		SharedPreferencesMgr.setIsSetAlarmClock(true);
	}

	/***
	 * 重新计算listView高度，让其融入ScrollView
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);

	}

	public static String getDeviceId(Context context) {
		return Settings.System.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);
	}

	public static byte[] decodeByBase64(String str){
		return Base64.decode(str, Base64.DEFAULT);
	}

	
	/**对文件进行base64加密，并输出加密后的字符串
	 * @param filePath
	 * @return
	 */
	public static String fileBase64(String filePath){
	    //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	        InputStream in = null;
	        byte[] data = null;
	        //读取图片字节数组
	        try 
	        {
	            in = new FileInputStream(filePath);        
	            data = new byte[in.available()];
	            in.read(data);
	            in.close();
	        } catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	        //返回Base64编码过的字节数组字符串
	        return Base64New.encode(data);
	    
	}
	
	public static Bitmap getImage(String path, Bitmap argBitmap, Context ctx) {
        
        	int thumbnailWidth = 600;
        	int thumbnailHeight = 400;
        
        	BitmapFactory.Options options = new BitmapFactory.Options(); 
        
            options.inJustDecodeBounds = true;
            options.outWidth = 600;
            options.outHeight = 400;
            options.inSampleSize = 2;

            BitmapFactory.decodeFile(path, options);
            if (options.outWidth > 0 && options.outHeight > 0) {
                // Now see how much we need to scale it down.
                int widthFactor = (options.outWidth + thumbnailWidth - 1)
                        / thumbnailWidth;
                int heightFactor = (options.outHeight + thumbnailHeight - 1)
                        / thumbnailHeight;

                widthFactor = Math.max(widthFactor, heightFactor);
                widthFactor = Math.max(widthFactor, 1);

                // Now turn it into a power of two.
                if (widthFactor > 1) {
                    if ((widthFactor & (widthFactor - 1)) != 0) {
                        while ((widthFactor & (widthFactor - 1)) != 0) {
                            widthFactor &= widthFactor - 1;
                        }

                        widthFactor <<= 1;
                    }
                }

                options.inSampleSize = widthFactor;
                options.inJustDecodeBounds = false;

                Bitmap bitmap = BitmapFactory.decodeFile(path,
                        options);
                if (bitmap == null) {
                    return argBitmap;
                }
                float scaleWidth = ((float) thumbnailWidth) / bitmap.getWidth();
                float scaleHeight = ((float) thumbnailHeight) / bitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return resizedBitmap;
            }
            return null;
	}
	public static String compressImage(String phtotoPath,Context context){
		String newPath =  Environment.getExternalStorageDirectory().getAbsolutePath()
				+"/"+ UUID.randomUUID().toString() + ".jpg";
		try{
			FileOutputStream fos = new FileOutputStream(newPath); 
			getImage(phtotoPath,null,context).compress(Bitmap.CompressFormat.PNG, 100, fos);
		}catch(Exception e){
			e.printStackTrace();
		}
		return newPath;
	}
//	public static String compressImage(String phtotoPath){
//		String newPath =  Environment.getExternalStorageDirectory().getAbsolutePath()
//				+"/"+ UUID.randomUUID().toString() + ".jpg";
//		File file = new File(newPath);
//		if(!file.exists()){
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		Logs.i("newPath"+newPath);
//			try {
//				FileOutputStream out = new FileOutputStream(file);				
//				FileInputStream fis = new FileInputStream(phtotoPath);
//				Bitmap bitmap  = BitmapFactory.decodeStream(fis);
//				int scale = 100;
//				if(fis.available()/1024/1024>1.5){
//					scale = 80;
//				}
//				bitmap.compress(Bitmap.CompressFormat.JPEG, scale, out);
//				
//				fis.close();
//				out.flush();
//				out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return newPath;
//	}
	/***
	 * 获取文件base64编码
	 * 
	 * @param in
	 * @return
	 */
	public static String getBase64(InputStream in) {
		if (in == null)
			return null;
		ByteArrayOutputStream outStream = null;
		byte[] dataArr = null;
		try {
			int size = 1024;
			outStream = new ByteArrayOutputStream();
			byte[] data = new byte[size];
			int count = -1;
			int jj = in.read(data, 0, size);
			while ((count = in.read(data, 0, size)) != -1)
				outStream.write(data, 0, count);
			data = null;
			dataArr = outStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outStream != null)
					outStream.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (dataArr == null)
			return null;
		//return Base64Coder.encodeLines(dataArr);
		//return Base64.encode(dataArr);
		return Base64New.encode(dataArr);
		//return Base64.encodeToString(dataArr, Base64.DEFAULT);
	}
	
	public static String getLocalMacAddress(Context context) {
		String macAddress = "88888888";

		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info.getMacAddress() != null) {
			macAddress = info.getMacAddress();
		}

		Log.e("simon", "MAC ADDRESS : " + macAddress);
		return macAddress;
	}
	/**
	 * @Description Checking if Mobile network is available
	 * @param Base Context
	 * @return true: mobile network is available, false: mobile network is unavailable.
	 */
	public static boolean isMobile(Context context){
		ConnectivityManager cm 	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni			= cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileAvail 	= ni.isAvailable();
		boolean isMobileConnect = ni.isConnected();
		return isMobileAvail && isMobileConnect;
	}
	/***
     * 图片的缩放方法
     * 
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                    double newHeight) {
            // 获取这个图片的宽和高
            float width = bgimage.getWidth();
            float height = bgimage.getHeight();
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算宽高缩放率
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                            (int) height, matrix, true);
            return bitmap;
    }
    
  /**
   * \MD5加密，32位  
   * @param str
   * @return
   */
    public static String MD5(String str)  
    {  
        MessageDigest md5 = null;  
        try  
        {  
            md5 = MessageDigest.getInstance("MD5"); 
        }catch(Exception e)  
        {  
            e.printStackTrace();  
            return "";  
        }  
          
        char[] charArray = str.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
          
        for(int i = 0; i < charArray.length; i++)  
        {  
            byteArray[i] = (byte)charArray[i];  
        }  
        byte[] md5Bytes = md5.digest(byteArray);  
          
        StringBuffer hexValue = new StringBuffer();  
        for( int i = 0; i < md5Bytes.length; i++)  
        {  
            int val = ((int)md5Bytes[i])&0xff;  
            if(val < 16)  
            {  
                hexValue.append("0");  
            }  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    }
    public static String formatDate(String str){
		//String strDate = "2013/5/16 9:19:33.44Z";  
    	String strDate = str;
	    String pat1 = "yyyy-MM-dd HH:mm:ss.SSS";  
	    String pat2 = "yyyy-MM-dd_HH:mm:ss.SSS";  
	    SimpleDateFormat sdf1 = new SimpleDateFormat(pat1);// 实例化模板对象  
	    SimpleDateFormat sdf2 = new SimpleDateFormat(pat2);// 实例化模板对象  
	    Date d = null;  
	    try {  
	        d = sdf1.parse(strDate);   
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	    return sdf2.format(d);// 将日期变为新的格式  
    }
}
