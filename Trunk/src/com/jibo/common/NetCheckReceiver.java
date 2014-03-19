package com.jibo.common;

import java.text.SimpleDateFormat;

import com.api.android.GBApp.R;
import com.jibo.activity.InitializeActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

/**
 * ���������жϼ���
 * @author will
 * @create 2013-4-25 ����9:52:48
 */
public class NetCheckReceiver extends BroadcastReceiver{ 
	/** android ������仯ʱ������Intent������  */
	public static final String netACTION = "android.net.conn.CONNECTIVITY_CHANGE"; 

	@Override 
	public void onReceive(final Context context, Intent intent){ 

		if(intent.getAction().equals(netACTION)){ 
			//Intent��ConnectivityManager.EXTRA_NO_CONNECTIVITY����ؼ��ֱ�ʾ�ŵ�ǰ�Ƿ������������� 
			//true ��������Ͽ�   false ��������û�жϿ� 
			boolean isBreak = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false); 
			Log.i("will", "isNetBreak:" + isBreak);
			if(!SharedPreferencesMgr.getIsAuto()) return;
			if(!isBreak){
				if(isWifi(context)){
					if(!Util.checkData(context)){
						if(isShowNotify()){
							showNotificaction(context);
						}
					}
				}
			}
		} 
	}
	
	/**
	 * �Ƿ���wifi����
	 * @param context
	 * @return
	 */
	public static boolean isWifi(Context context){
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();  
		if (networkInfo != null && networkInfo.isConnected()) {  
			String type = networkInfo.getTypeName();  
			if (type.equalsIgnoreCase("WIFI")) {  
				return true;
			} else if (type.equalsIgnoreCase("MOBILE")) {  
				return false;
			}  
		}
		return false;
	}
	
	 /** 
     * ���һ��notification 
     */  
    private void showNotificaction(Context context) {  
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
        // ����һ��Notification  
        Notification notification = new Notification();  
        // ������ʾ���ֻ����ϱߵ�״̬����ͼ��  
        notification.icon = R.drawable.icon;  
        // ����ǰ��notification���ŵ�״̬���ϵ�ʱ����ʾ����  
        notification.tickerText = context.getString(R.string.ln_tips);  
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        
        Intent intent = new Intent(context, InitializeActivity.class);  
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);  
        // ���״̬����ͼ����ֵ���ʾ��Ϣ����  
        notification.setLatestEventInfo(context, context.getString(R.string.ln_title), context.getString(R.string.ln_msg), pendingIntent);  
        manager.notify(1, notification);  
    }  
    
    private boolean isShowNotify(){
    	String lnCount = SharedPreferencesMgr.getLNCount();
    	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    	String current_time = sf.format(System.currentTimeMillis());
    	if(TextUtils.isEmpty(lnCount)){
    		SharedPreferencesMgr.setLNCount(1, current_time);
    		return true;
    	}
    	
    	String[] lnCounts = lnCount.split("&");
    	int count = Integer.parseInt(lnCounts[0]);
    	String old_time = lnCounts[1];
    	if(count >= 3) return false;
    	
    	if(!old_time.equals(current_time)) {
    		SharedPreferencesMgr.setLNCount(++count, current_time);
    		return true;
    	}
    	
    	return false;
    }

}
