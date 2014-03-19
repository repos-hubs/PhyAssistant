package com.jibo.util;

import android.os.Handler;
import android.view.View;

public abstract class ThreadHandler {
	private Handler handler = new Handler();
	
	public ThreadHandler() {
		super();
		doit();
	}

	public void doit() {
		new Thread() {
			public void run() {
				execute();
				handler.post(new Runnable(){		
			// 新线程中发送一个Runnable给创建Handler的线程, 
			//创建Handler的线程就会执行Runnable的run()方法
					public void run() {
						postexecute();
					}
				});
			}
		}.start();
	}

	/**
	 * 子线程中执行的
	 */
	public abstract void execute();
	/**
	 * 跟新ui
	 */
	public abstract void postexecute();
}
