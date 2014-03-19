package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.PayInfoEntity;

public class SurveyGetPayInfo extends SoapDataPaser {
	private PayInfoEntity payInfo;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject payObj = (SoapObject) result.getProperty("NewGetUserSurveyinfoResult");
		payInfo = new PayInfoEntity();
		
		String propertyValue = payObj.getProperty("Name").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setName(propertyValue);
		} else {
			payInfo.setName("");
		}
		
		propertyValue = payObj.getProperty("phone").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setPhone(propertyValue);
		} else {
			payInfo.setPhone("");
		}
		
		propertyValue = payObj.getProperty("region").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setRegion(propertyValue);
		} else {
			payInfo.setRegion("");
		}
		
		propertyValue = payObj.getProperty("city").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setCity(propertyValue);
		} else {
			payInfo.setCity("");
		}
		
		propertyValue = payObj.getProperty("hospital").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setHospital(propertyValue);
		} else {
			payInfo.setHospital("");
		}
		
		propertyValue = payObj.getProperty("department").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setDepartment(propertyValue);
		} else {
			payInfo.setDepartment("");
		}
		
		propertyValue = payObj.getProperty("professional").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setProfessional(propertyValue);
		} else {
			payInfo.setProfessional("");
		}
		
		propertyValue = payObj.getProperty("payWay").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setPayWay(propertyValue);
		} else {
			payInfo.setPayWay("");
		}
		
		propertyValue = payObj.getProperty("payAccount").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setPayAccount(propertyValue);
		} else {
			payInfo.setPayAccount("");
		}
		
		propertyValue = payObj.getProperty("isCheck").toString();
		if(!"anyType{}".equals(propertyValue)) {
			payInfo.setIsCheck(propertyValue);
		} else {
			payInfo.setIsCheck("");
		}
	}
	public PayInfoEntity getPayInfo() {
		return payInfo;
	}
	public void setPayInfo(PayInfoEntity payInfo) {
		this.payInfo = payInfo;
	}

}
