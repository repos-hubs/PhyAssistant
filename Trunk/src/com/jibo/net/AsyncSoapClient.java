/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.jibo.net;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;

/**
 * soap����ͻ��ˣ�ʹ���̳߳أ����Բ�������������,ͬʱ����ȡ����������ҳ
 * ����������
 */
public class AsyncSoapClient {

	// private static final int DEFAULT_MAX_CONNECTIONS = 10;

	/** ��������̳߳� */
	private ThreadPoolExecutor threadPool;
	/** �����Map��keyΪ���������ҳ�� */
	private Map<Context, List<WeakReference<Future<?>>>> requestMap;

	/**
	 * Creates a new AsyncSoapClient.
	 */
	public AsyncSoapClient() {

		threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		requestMap = new ConcurrentHashMap<Context, List<WeakReference<Future<?>>>>();

	}

	/**
	 * Overrides the threadpool implementation used when queuing/pooling
	 * requests. By default, Executors.newCachedThreadPool() is used.
	 * 
	 * @param threadPool
	 *            an instance of {@link ThreadPoolExecutor} to use for
	 *            queuing/pooling requests.
	 */
	public void setThreadPool(ThreadPoolExecutor threadPool) {
		// this.threadPool = threadPool;
	}

	/**
	 * Cancels any pending (or potentially active) requests associated with the
	 * passed Context.
	 * <p>
	 * <b>Note:</b>
	 * �������ֻ��Ӱ�쵽���õ��Ǹ�context�������ڲ�����Ҫ��ʱ����ã�����
	 * onstop ondestroy
	 * 
	 * @param context
	 *            the android Context instance associated to the request.
	 * @param mayInterruptIfRunning
	 *            specifies if active requests should be cancelled along with
	 *            pending requests.
	 */
	public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
		List<WeakReference<Future<?>>> requestList = requestMap.get(context);
		if (requestList != null) {
			for (WeakReference<Future<?>> requestRef : requestList) {
				Future<?> request = requestRef.get();
				if (request != null) {
					request.cancel(mayInterruptIfRunning);
				}
			}
		}
		requestMap.remove(context);
	}

	/**
	 * ȡ�����е�����
	 * */
	public void cancelAll() {
		Collection<List<WeakReference<Future<?>>>> requestList = requestMap
				.values();
		if (requestList != null) {
			Iterator<List<WeakReference<Future<?>>>> requestList2 = requestList
					.iterator();
			if (requestList2.hasNext()) {
				List<WeakReference<Future<?>>> list = requestList2.next();
				if (list != null) {
					for (WeakReference<Future<?>> requestRef : list) {
						Future<?> request = requestRef.get();
						if (request != null) {
							request.cancel(true);
						}
					}
				}
			}
		}
		try {
			threadPool.shutdownNow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		threadPool = null;
		requestMap.clear();
		requestMap = null;
	}

	// ExecutorService executorService;
	/** ����soap���ݣ�Ŀǰʹ��ksoap����û�г�ʱ�����쳣������Ҫ������д�� */

	public void sendRequest(String url, int methodId, Properties propertyInfo,
			AsyncSoapResponseHandler responseHandler, Context context,
			boolean islazy) {
		Future<?> request = threadPool.submit(new AsyncSoapRequest(url,
				methodId, propertyInfo, responseHandler, islazy));
		if (context != null) {
			// Add request to request map
			List<WeakReference<Future<?>>> requestList = requestMap
					.get(context);
			if (requestList == null) {
				requestList = new LinkedList<WeakReference<Future<?>>>();
				requestMap.put(context, requestList);
			}
			requestList.add(new WeakReference<Future<?>>(request));
			// TODO: Remove dead weakrefs from requestLists?
		}
	}

	public void sendRequest(String url, int methodId, Properties propertyInfo,
			AsyncSoapResponseHandler responseHandler, Context context) {
		Future<?> request = threadPool.submit(new AsyncSoapRequest(url,
				methodId, propertyInfo, responseHandler, false));
		if (context != null) {
			// Add request to request map
			List<WeakReference<Future<?>>> requestList = requestMap
					.get(context);
			if (requestList == null) {
				requestList = new LinkedList<WeakReference<Future<?>>>();
				requestMap.put(context, requestList);
			}
			requestList.add(new WeakReference<Future<?>>(request));
			// TODO: Remove dead weakrefs from requestLists?
		}
	}
	
}
