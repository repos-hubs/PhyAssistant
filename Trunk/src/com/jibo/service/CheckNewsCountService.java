package com.jibo.service;

import java.util.Properties;

import com.jibo.GBApplication;
import com.jibo.common.DeviceInfo;
import com.jibo.common.SoapRes;
import com.jibo.data.NewsCountPaser;
import com.jibo.dbhelper.InitializeAdapter;
import com.jibo.dbhelper.NewsSQLAdapter;
import com.jibo.net.AsyncSoapResponseHandler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.IBinder;
import android.os.RemoteCallbackList;

public class CheckNewsCountService extends Service {
	private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
	private InitializeAdapter initializeAdapter;
	private CheckUpadateHandler baseHandler;
	private Context mContext;
	public static final int RESULT_UPDATE_NEWS = 2;
	
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
		checkNewsUpdate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mCallbacks.kill();
	}
	/**
		  * 检验是否是最新的版本
		  * */
		 private void checkNewsUpdate() {
		  try {
		   NewsSQLAdapter newsAdapter = new NewsSQLAdapter(this);
		   if (initializeAdapter == null) {
		    initializeAdapter = new InitializeAdapter(this, 1);
		    baseHandler = new CheckUpadateHandler();
		   }
		   if (DeviceInfo.instance.isNetWorkEnable()) {
		    Properties propertyInfo = new Properties();
		    propertyInfo.put("id",
		      newsAdapter.getLocalMaxId());
		    
		    GBApplication.gbapp.soapClient.sendRequest(SoapRes.URLNews,
		      SoapRes.REQ_ID_GET_NEWS_COUNT, propertyInfo,
		      baseHandler, this);
		   }
		  } catch (SQLiteException e) {

		  }
		 }
	
	private class CheckUpadateHandler extends AsyncSoapResponseHandler {
		@Override
		public void onSuccess(Object content, int reqId) {
			try {
				if (content != null) {
					int N = mCallbacks.beginBroadcast();
					for (int i = 0; i < N; i++) {
						if (content instanceof NewsCountPaser) {
							mCallbacks.getBroadcastItem(i).showResult(
									RESULT_UPDATE_NEWS, ((NewsCountPaser)content).getNewsCount());
						} 
					}
				}
			} catch (Exception e) {
				System.out.println("exception     "+e.getMessage());
			}
			super.onSuccess(content, reqId);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
		}
	}
}
