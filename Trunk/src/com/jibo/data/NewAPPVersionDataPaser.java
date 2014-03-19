package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/***
 * 获取应用程序更新回调解析
 * 
 * @author simon
 * 
 */
public class NewAPPVersionDataPaser extends SoapDataPaser {

	private String rescode;
	private String errMsg;
	private String updateCode;
	private String msgTitle;
	private String msgDesc;
	private String url;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_APP_NEW_VERSION);

		if (detail.getPropertyCount() > 0) {
			rescode = detail.getProperty("rescode").toString();
			if (rescode.equals("200")) {
				errMsg = detail.getProperty("error").toString();
				updateCode = detail.getProperty("updateCode").toString();
				msgTitle = detail.getProperty("msgTitle").toString();
				msgDesc = detail.getProperty("msgDesc").toString();
				url = detail.getProperty("redirectUrl").toString();
			}
		}
	}

	public String getRescode() {
		return rescode;
	}

	public void setRescode(String rescode) {
		this.rescode = rescode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getUpdateCode() {
		return updateCode;
	}

	public void setUpdateCode(String updateCode) {
		this.updateCode = updateCode;
	}

	public String getMsgTitle() {
		return msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public String getMsgDesc() {
		return msgDesc;
	}

	public void setMsgDesc(String msgDesc) {
		this.msgDesc = msgDesc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
