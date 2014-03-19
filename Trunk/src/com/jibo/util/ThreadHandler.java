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
			// ���߳��з���һ��Runnable������Handler���߳�, 
			//����Handler���߳̾ͻ�ִ��Runnable��run()����
					public void run() {
						postexecute();
					}
				});
			}
		}.start();
	}

	/**
	 * ���߳���ִ�е�
	 */
	public abstract void execute();
	/**
	 * ����ui
	 */
	public abstract void postexecute();
}
