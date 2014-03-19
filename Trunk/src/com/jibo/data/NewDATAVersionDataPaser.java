package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/***
 * 获取db and source file更新回调解析类
 * 
 * @author simon
 * 
 */
public class NewDATAVersionDataPaser extends SoapDataPaser {

	private String rescode;
	private String errMsg;
	private String latestDataVersion;
	private String url;
	private String updateCode;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_DATA_NEW_VERSION);

		if (detail.getPropertyCount() > 0) {
			rescode = detail.getProperty("rescode").toString();
			if (null != rescode && rescode.equals("200")) {
				errMsg = detail.getProperty("error").toString();
				latestDataVersion = detail.getProperty("latestDataVersion")
						.toString();
				url = detail.getProperty("redirectUrl").toString();
				updateCode = detail.getProperty("updateCode").toString();
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

	public String getLatestDataVersion() {
		return latestDataVersion;
	}

	public void setLatestDataVersion(String latestDataVersion) {
		this.latestDataVersion = latestDataVersion;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
