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

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;

import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.SoapRes;
import com.jibo.util.JiBoException;
import com.jibo.util.Logs;

/**
 * soap 数据请求类
 * 
 * @author peter.pan
 * */
class AsyncSoapRequest implements Runnable {
	boolean islazy = false;
	/** 请求的url */
	private final String url;
	/** 请求的方法Id */
	private final int methodId;
	/** 请求的方法 */
	private String method;
	/** 请求的结果的处理类 */
	private final AsyncSoapResponseHandler responseHandler;
	/** 请求传递的参数 */
	private final Properties propertyInfos;
	/** Soap的名称空间 */
	private static String NAME_SPACE = SoapRes.NAMESPACE;

	/**
	 * 必须首先设置名称空间
	 * 
	 * @param name
	 *            名称空间
	 * */
	public static void setNameSpace(String name) {
		NAME_SPACE = name;

	}

	/**
	 * @param url
	 *            请求的url
	 * @param methodID
	 *            请求的方法Id
	 * @param propertyInfos
	 *            请求传递的参数
	 * @param responseHandler
	 *            请求的结果的处理类
	 * */
	public AsyncSoapRequest(String url, int methodID, Properties propertyInfos,
			AsyncSoapResponseHandler responseHandler) {
		this.url = url;
		this.methodId = methodID;
		method = SoapRes.getMethod(methodId);
		this.propertyInfos = propertyInfos;
		this.responseHandler = responseHandler;
	}

	public AsyncSoapRequest(String url, int methodID, Properties propertyInfos,
			AsyncSoapResponseHandler responseHandler, boolean lazy) {
		this.url = url;
		this.methodId = methodID;
		method = SoapRes.getMethod(methodId);
		this.propertyInfos = propertyInfos;
		this.responseHandler = responseHandler;
		this.islazy = lazy;
	}

	public void run() {
		boolean notify = url.toLowerCase().contains("getimage")||url.toLowerCase().contains("stick");
		Exception ex = null;
		Boolean rec = null;
		do {
			if(rec!=null&&rec)rec = null;
			try {
				if (islazy) {
					// Thread.currentThread().sleep(55500);
					Logs.i("=== sleep b-a");
				}
				if (NAME_SPACE == null) {
					System.out.println(AsyncSoapRequest.class.getName()
							+ " no name space");
					return;
				}
				if (responseHandler != null) {
					responseHandler.sendStartMessage();
				}
				makeRequest();
			}  catch (Exception e) {
				rec = true;
				if(notify)
				responseHandler.sendReloadMessage( GBApplication.gbapp.getString(R.string.retryweb));
				
				continue;
			}finally {
				if (ex != null)
					responseHandler.sendFailureMessage(ex, null);
				// ex.printStackTrace();
				if (responseHandler != null) {
					responseHandler.sendFinishMessage();
				}				
			}
		} while (rec!=null&&notify);
	}

	/**
	 * 发起请求
	 * */
	private void makeRequest() throws Exception {

		if (url.toLowerCase().endsWith("paperlist")
				|| url.contains("JournalWatch.asmx")
				|| url.contains("druginteractionservice.asmx")) {
			NAME_SPACE = "http://tempuri.org/";
		} else {
			NAME_SPACE = "http://www.pda.com/pda/";
		}
		if (!Thread.currentThread().isInterrupted()) {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			SoapObject soapObject = new SoapObject(NAME_SPACE, method);
			if (propertyInfos != null) {
				Iterator<Entry<Object, Object>> it = propertyInfos.entrySet()
						.iterator();
				Entry<Object, Object> entry = null;
				while (it.hasNext()) {
					entry = it.next();
					soapObject.addProperty(entry.getKey().toString(), entry
							.getValue().toString());
				}
			}

//			Logs.i("NAME_SPACE + url=" + url + ";method=" + method + " "
//					+ soapObject);
			AndroidHttpTransport ht = new AndroidHttpTransport(url);
			envelope.bodyOut = soapObject;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(soapObject);
			System.out.println("url visit " + NAME_SPACE + method);
			long a;
			long b;
			Logs.i("b-a start" + (a = System.currentTimeMillis()) + " "
					+ new Date(a).toString());
			ht.call(NAME_SPACE + method, envelope);
			Logs.i("" + (Logs.a = b = System.currentTimeMillis()));
			Logs.i(" b-a " + (b - a) / 1000L + " " + NAME_SPACE + method);
			Logs.i(" b-a ==== " + System.currentTimeMillis() / 1000);
			if (Thread.currentThread().isInterrupted()) {
				// 用户取消

			} else {
				// Log.i("lushan",
				// (System.currentTimeMillis()-NewsGetInfoByIdPaser.mill)+"");
				// NewsGetInfoByIdPaser.mill = System.currentTimeMillis();
				if (envelope.getResponse() != null) {
					// 数据请求成功
					responseHandler.sendResponseMessage(methodId, envelope);
				}
			}
		}
	}

}