package com.jibo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ���Ѹ�������
 * 
 * @author simon
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {

	

	@Override
	public void onReceive(Context context, Intent intent) {
		 Intent service = new Intent(context, AlarmService.class);  
		 context.startService(service);  
	}

}
