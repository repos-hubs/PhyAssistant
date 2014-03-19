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

package com.jibo.base.src.request.impl.soap;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.base.src.request.CursorParser;
import com.jibo.base.src.request.CursorParser.CursorResult;
import com.jibo.data.SoapDataPaser;
import com.jibo.data.SoapPaserFactory;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;

/**
 * Soap消息处理handler
 * @author peter.pan
 */
public class AsyncResponseHandler {
    private static final int SUCCESS_MESSAGE = 0;
    private static final int FAILURE_MESSAGE = 1;
    private static final int START_MESSAGE = 2;
    private static final int FINISH_MESSAGE = 3;

    private Handler handler;

    /**
     * Creates a new AsyncSoapResponseHandler
     */
    public AsyncResponseHandler() {
        // Set up a handler to post events back to the correct thread if possible
        if(Looper.myLooper() != null) {
            handler = new Handler(){
                public void handleMessage(Message msg){
                    AsyncResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }


    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when the request is started, override to handle in your own code
     */
    public void onStart() {}

    /**
     * Fired in all cases when the request is finished, after both success and failure, override to handle in your own code
     */
    public void onFinish() {}

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param content the body of the HTTP response from the server
     */
    public void onSuccess(Object content) {}

    public void onSuccess(Object content,int reqId) {onSuccess(content);}

    /**
     * Fired when a request fails to complete, override to handle in your own code
     * @param error the underlying cause of the failure
     * @param content the response body, if any
     */
    public void onFailure(Throwable error, String content) {
        // By default, call the deprecated onFailure(Throwable) for compatibility
       
    }


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    protected void sendSuccessMessage(Object responseBody,int methodId) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{responseBody,methodId}));
    }

    public void sendFailureMessage(Throwable e, String responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    protected void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    protected void sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null));
    }


    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    protected void handleSuccessMessage(Object responseBody,int methodId) {
        onSuccess(responseBody,methodId);
    }

    protected void handleFailureMessage(Throwable e, String responseBody) {
        onFailure(e, responseBody);
    }



    // Methods which emulate android's Handler and Message methods
    protected void handleMessage(Message msg) {
        switch(msg.what) {
            case SUCCESS_MESSAGE:
            	Object[] rep = (Object[])msg.obj;
                handleSuccessMessage(rep[0],(Integer)rep[1]);
                break;
            case FAILURE_MESSAGE:
                Object[] repsonse = (Object[])msg.obj;
                handleFailureMessage((Throwable)repsonse[0], (String)repsonse[1]);
                break;
            case START_MESSAGE:
                onStart();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
        }
    }

    protected void sendMessage(Message msg) {
        if(handler != null){
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if(handler != null){
            msg = this.handler.obtainMessage(responseMessage, response);
        }else{
            msg = new Message();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }


    /**
     * 给AsyncSoapRequest回调的接口
     * @param methodId 方法Id
     * @param response 请求结果处理类
     * @throws Exception 错误信息
     */
	void sendResponseMessage(int methodId, Object response) throws Exception {
		Object o = null;
		if (response instanceof SoapSerializationEnvelope) {
			SoapSerializationEnvelope resp = (SoapSerializationEnvelope) response;
			o = SoapPaserFactory.paserData(methodId, resp);
		}else if(response instanceof CursorResult){
			o = CursorParser.parseCursor((CursorResult)response);
		}
		if (o == null) {
			sendFailureMessage(null, "paser class not found");
		} else {
			sendSuccessMessage(o, methodId);
		}
	}
}