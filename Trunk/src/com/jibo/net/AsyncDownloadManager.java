package com.jibo.net;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;

public class AsyncDownloadManager{
	private ThreadPoolExecutor threadPool;
	public AsyncDownloadManager() {
		threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}
	
	public void executePackage(Context mContext, String action, String url, BaseResponseHandler handler) {
		threadPool.execute(new AsyncDownloadRnnable(mContext, action, url, handler));
	}
	
}
