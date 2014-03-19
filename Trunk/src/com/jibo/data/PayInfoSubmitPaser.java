package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class PayInfoSubmitPaser extends SoapDataPaser {

	private String submitResult;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("NewupdateUserSurveyinfoResult");
		String propertyValue = detail.getProperty("result").toString();
		if(!"anyType{}".equals(propertyValue)) {
			submitResult = propertyValue;
		} else {
			submitResult = "";
		}
		
		System.out.println("PayInfoSubmitPaser    "+submitResult);
	}
	public String getSubmitResult() {
		return submitResult;
	}
	public void setSubmitResult(String submitResult) {
		this.submitResult = submitResult;
	}

}
