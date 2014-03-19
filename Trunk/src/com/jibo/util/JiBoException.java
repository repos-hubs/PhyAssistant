/**
 * 
 */
package com.jibo.util;

import com.api.android.GBApp.R;

import android.util.Log;


/**
 * @author panyongjun
 * 自定义的错误
 */
public class JiBoException extends Exception {

	public int mErrCode;
	public static final int ERR_NO_AVAILABLE_NETWORK=1;
	
	public static final int ERR_TIMEOUT=2;

	/**soap数据解析错误*/
	public static final int ERR_SOAP_PASER=3;
	
	/**soap IO*/
	public static final int ERR_SOAP_IO=4;
	
	
	public JiBoException(Exception e)
	{
		//
		saveLog( e);
	}
	public JiBoException(Exception e,int code)
	{
		//
		saveLog( e);
		mErrCode =code;
	}
	public JiBoException()
	{
		
	}
	/**
	 * 返回str资源id;
	 * */
	public  int getErrRes()
	{
		switch (mErrCode)
		{
		case ERR_TIMEOUT:
			return R.string.networkNotReachable;
		case ERR_SOAP_PASER:
		case ERR_SOAP_IO:
			return R.string.losedownload;
		}
		return 0;
	}
	
	/**
	 * 存储错误信息
	 * */
	public void saveLog(Exception e)
	{
		//
		e.printStackTrace();
		Log.e("JiBoException ", e.toString());
	}
}
