package com.jibo.service;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jibo.common.Constant;

public class InstallBroadCast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
			System.out.println("    ***   ACTION_PACKAGE_ADDED");
			Toast.makeText(context, "有应用被添加", Toast.LENGTH_LONG).show();
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
			System.out.println("    ***   ACTION_PACKAGE_REMOVED");
			Toast.makeText(context, "有应用被删除", Toast.LENGTH_LONG).show();
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
			String packageName = intent.getDataString();
			if(packageName!=null&&packageName.contains("com.api.android.GBApp")) {
				Toast.makeText(context, "有应用被替换"+packageName, Toast.LENGTH_LONG).show();
				File file = new File(Constant.DATA_PATH);
			} else {
				Toast.makeText(context, "有其他应用被替换"+packageName, Toast.LENGTH_LONG).show();
			}
		}
	}
}
