package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

/***
 * 下载程序对应的db文件后回调
 * @author simon
 *
 */
public class NewDBDataPaser extends SoapDataPaser {
	private String url;
	private String type;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("getNewUpdateResult");
		if(detail.getPropertyCount()>0) {
			url = detail.getProperty("update_url").toString();
			type = detail.getProperty("dataType").toString();
		}
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
