package com.jibo.common;



import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * 移动设备相关信息类
 * */
public class DeviceInfo {

	private Context context;
	private float scale;
	private float screenWidth;
	private float screenHeight;
	
	public DeviceInfo(Context c)
	{
		context =c;
		instance=this;
		scale = context.getResources().getDisplayMetrics().density;
	}
	
	public float getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(float screenWidth) {
		this.screenWidth = screenWidth;
	}

	public float getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(float screenHeight) {
		this.screenHeight = screenHeight;
	}

	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}

	public static DeviceInfo instance;
	/**是否有可用的网络*/
	public boolean isNetWorkEnable()
	{
		return isWifi()|| isMobile();
	}
	/**
	 * @Description Checking if wifi is available
	 * @param Base Context
	 * @return true: wifi network is available, false: wifi network is unavailable.
	 */
	public boolean isWifi(){
		ConnectivityManager cm 	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni 			= cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiAvail 	= ni.isAvailable();
		boolean isWifiConnect 	= ni.isConnected();
		return isWifiAvail && isWifiConnect;
	}
	
	/**
	 * @Description Checking if Mobile network is available
	 * @param Base Context
	 * @return true: mobile network is available, false: mobile network is unavailable.
	 */
	private boolean isMobile(){
		ConnectivityManager cm 	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni			= cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileAvail 	= ni.isAvailable();
		boolean isMobileConnect = ni.isConnected();
		return isMobileAvail && isMobileConnect;
	}
	
	/**是否有sd卡*/
	public static boolean isSdcardExist() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 获取屏幕分辨率
	 * */
	public static int getResolution(Activity act){
		int Width  = 0;
		int Height = 0;
		Width  = act.getWindowManager().getDefaultDisplay().getWidth();
		Height = act.getWindowManager().getDefaultDisplay().getHeight();
		return Width * Height;
	}
	
	public static boolean bChineseVersion()
	{
		return instance.context.getResources().getConfiguration().locale.toString().contains("zh");
	}
}
