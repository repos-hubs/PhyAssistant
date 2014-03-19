package com.jibo.activity;

import java.util.Calendar;

import com.jibo.common.Constant;
import com.jibo.common.Util;
import com.jibo.util.Logs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		if (action.equals("android.intent.action.BOOT_COMPLETED")) {
			Logs.i("boot completed ");
			Util.bindAlertAlarm(context);
		}
	}
}
