package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class UpdateInviteCodePaser extends SoapDataPaser {

	private String rescode = "";
	private String error = "";
	private String url = "";
	private String expiredDate;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject resultList = (SoapObject) result.getProperty("UpdateInviteCode2Result");
		
		String propertyValue = resultList.getProperty("rescode").toString();
		propertyValue = !"anyType{}".equals(propertyValue.toString()) ? propertyValue
				.toString() : "";
		if(!"anyType{}".equals(propertyValue)) {
			rescode = propertyValue;
		} 
		propertyValue = resultList.getProperty("error").toString();
		propertyValue = !"anyType{}".equals(propertyValue.toString()) ? propertyValue
				.toString() : "";
		if(!"anyType{}".equals(propertyValue)) {
			error = propertyValue;
		}
		
		propertyValue = resultList.getProperty("user_address").toString();
		propertyValue = !"anyType{}".equals(propertyValue.toString()) ? propertyValue
				.toString() : "";
		if(!"anyType{}".equals(propertyValue)) {
			url = propertyValue;
		}
		
		propertyValue = resultList.getProperty("expiredDate").toString();
		propertyValue = !"anyType{}".equals(propertyValue.toString()) ? propertyValue
				.toString() : "";
				expiredDate = propertyValue;
		
	}
	public String getRescode() {
		return rescode;
	}
	public void setRescode(String rescode) {
		this.rescode = rescode;
	}
	public String getError() {
		return error;
	}
	public String getUrl(){
		return url;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getExpiredDate() {
		return expiredDate;
	}
	
}
