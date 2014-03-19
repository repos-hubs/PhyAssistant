package com.jibo.net;

import com.jibo.activity.BaseActivity;
import com.jibo.common.DialogRes;

/**
 * 与baseactivity相关联，处理一些公共逻辑
 * @author peter.pan
 * */
/**
 * @author Administrator
 * 
 */
public class BaseResponseHandler extends AsyncSoapResponseHandler {

	/** 发起请求的BaseActivity 实例 */
	private BaseActivity act;
	/** 可选择dialog，默认DIALOG_WAITING_FOR_DATA */
	private int dialogId = DialogRes.DIALOG_WAITING_FOR_DATA;//
	/** 默认显示dialog */
	private boolean flag = true;//

	/**
	 * @param act
	 *            发起请求的BaseActivity 实例
	 */
	public BaseResponseHandler(BaseActivity act) {
		super();
		this.act = act;
	}

	/**
	 * @param act
	 *            发起请求的BaseActivity 实例
	 * @param dialogId
	 *            显示的dialogID
	 */
	public BaseResponseHandler(BaseActivity act, int dialogId) {
		super();
		this.act = act;
		this.dialogId = dialogId;
	}

	/**
	 * @param act
	 *            发起请求的BaseActivity 实例
	 * @param flag
	 *            是否显示dialog
	 */
	public BaseResponseHandler(BaseActivity act, boolean flag) {
		super();
		this.act = act;
		this.flag = flag;
	}
    /**
     * Fired when the request is started, override to handle in your own code
     */
    public void onStart() 
    {    	
    	if(act!=null)
    	{
    		try {
    			act.curReqTimes+=1;
        		if(dialogId!=-1&&flag)
        		act.showDialog(dialogId);
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    		
    	}
    }

	/**
	 * Fired in all cases when the request is finished, after both success and
	 * failure, override to handle in your own code
	 */
	public void onFinish() {
		if (act != null && !act.isFinishing()) {
			act.curReqTimes -= 1;
			if (act.curReqTimes == 0) {
				if (dialogId != -1 && flag) {
					act.removeDialog(dialogId);
				}
			}
		}
	}

	/**
	 * Fired when a request returns successfully, override to handle in your own
	 * code
	 * 
	 * @param content
	 *            the body of the HTTP response from the server
	 */
	public void onSuccess(Object content, int methodId) {
		if (act != null && !act.isFinishing()) {
			act.onReqResponse(content, methodId);
		}
	}

	/**
	 * Fired when a request fails to complete, override to handle in your own
	 * code
	 * 
	 * @param error
	 *            the underlying cause of the failure
	 * @param content
	 *            the response body, if any
	 */
	public void onFailure(Throwable error, String content) {
		// By default, call the deprecated onFailure(Throwable) for
		// compatibility
		if (act != null && !act.isFinishing())
			act.onErrResponse(error, content, false);
	}
}
