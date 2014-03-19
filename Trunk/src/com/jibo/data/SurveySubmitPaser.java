package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class SurveySubmitPaser extends SoapDataPaser {
	private String submitResult;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("NewSubmitSurveyResult");
		String propertyValue = detail.getProperty("result").toString();
		if(!"anyType{}".equals(propertyValue)) {
			submitResult = propertyValue;
		} else {
			submitResult = "";
		}
		System.out.println("submitResult    "+submitResult);//Success 1
	}
	public String getSubmitResult() {
		return submitResult;
	}
	public void setSubmitResult(String submitResult) {
		this.submitResult = submitResult;
	}

}
