package com.jibo.service;


import java.util.ArrayList;
import java.util.Properties;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.text.TextUtils;
import android.util.Log;

import com.jibo.GBApplication;
import com.jibo.common.DeviceInfo;
import com.jibo.common.SoapRes;
import com.jibo.data.NewAPPVersionDataPaser;
import com.jibo.data.NewDBDataPaser;
import com.jibo.data.VersionDataParser;
import com.jibo.data.entity.DownloadPacketEntity;
import com.jibo.dbhelper.InitializeAdapter;
import com.jibo.net.AsyncSoapResponseHandler;

public class CheckAPPUpdateService extends Service {
	public static final String ACTION_CALCUlATE = "action_calculate";
	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
	private InitializeAdapter initializeAdapter;
	private CheckUpadateHandler baseHandler;
	private Context mContext;
	private DownloadPacketEntity entity;
	public static final int NO_UPDATE = 0;//--------没有更新
	public static final int NEED_TO_UPDATE = 1;//---有更新
	public static final int MUST_TO_UPDATE = 2;//---必须更新，强制更新
	
	private IService.Stub mBinder = new IService.Stub() {
		@Override
		public void unregisterCallback(ICallback cb) {
			if (cb != null) {
				mCallbacks.unregister(cb);
			}
		}

		@Override
		public void registerCallback(ICallback cb) {
			if (cb != null) {
				mCallbacks.register(cb);
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		/*
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				checkDataUpdate();
			}
			
		}).start();
		*/
		checkDataUpdate();
	}

	@Override
	public void onDestroy() {
		mCallbacks.kill();
		super.onDestroy();
	}

	/**
	 * 检验是否是最新的版本
	 * */
	private void checkDataUpdate() {
		
		Log.i("simon", "启动AIDL");
		try {
			if (initializeAdapter == null) {
				initializeAdapter = new InitializeAdapter(this, 1);
				baseHandler = new CheckUpadateHandler();
			}
			if (DeviceInfo.instance.isNetWorkEnable()) {
				Properties propertyInfo = new Properties();
				String verName = initializeAdapter.getCurrentVerName();
				System.out.println("verName  **   "+verName);
				
				propertyInfo.put("appName", "集博应用");
				propertyInfo.put("version", verName);
				propertyInfo.put("platform", "android");
				propertyInfo.put("lang", "cn");
				
				GBApplication.gbapp.soapClient
						.sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_APP_NEW_VERSION,
								propertyInfo, baseHandler, this,true);
				
//				propertyInfo.put(SoapRes.KEY_NEW_P_VERSION, verName);
//				GBApplication.gbapp.soapClient
//						.sendRequest(SoapRes.URLNewVersion, SoapRes.REQ_ID_NEW_VERSION,
//								propertyInfo, baseHandler, this);
			}
		} catch (SQLiteException e) {

		}
	}

	
	private class CheckUpadateHandler extends AsyncSoapResponseHandler {
		private ArrayList<DownloadPacketEntity> list;

		@Override
		public void onSuccess(Object content, int reqId) {
			try {
				if (content != null) {
					if (content instanceof NewAPPVersionDataPaser) {
						NewAPPVersionDataPaser result = (NewAPPVersionDataPaser) content;
						String resCode = result.getRescode();
						if(null!=resCode&&resCode.equals("200")){
							int N = mCallbacks.beginBroadcast();
							int updateCode = TextUtils.isEmpty(result.getUpdateCode())?0:Integer.parseInt(result.getUpdateCode());
							for (int i = 0; i < N; i++) {
								mCallbacks.getBroadcastItem(i).showResult(
										updateCode, result.getUrl());
							}
						}
					}
//					if (content instanceof NewVersionDataPaser) {
//						NewVersionDataPaser vd = (NewVersionDataPaser) content;
//						String type = vd.getType();
//						int N = mCallbacks.beginBroadcast();
//						if (type != null && type.equals("fulldata")&& !"anyType{}".equals(vd.getUrl())) {
//							System.out.println("url   "+vd.getUrl());
//							for (int i = 0; i < N; i++) {
//								mCallbacks.getBroadcastItem(i).showResult(
//										NEED_TO_UPDATE, type);
//							}
//						} else {
//							for (int i = 0; i < N; i++) {
////								mCallbacks.getBroadcastItem(i).showResult(
////										NO_UPDATE, type);
//								mCallbacks.getBroadcastItem(i).showResult(
//										NEED_TO_UPDATE, type);
//							}
//						}
//						
//						
//					}
				}
			} catch (Exception e) {
			}
			super.onSuccess(content, reqId);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
		}
	}
}
