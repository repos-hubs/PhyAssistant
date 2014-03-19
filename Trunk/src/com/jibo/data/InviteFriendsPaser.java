package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class InviteFriendsPaser extends SoapDataPaser {
	private String rescode = "";
	private String error = "";
	private String inviteUserCount;
	private String inviteCode = "";
	private String expiredDate = "";

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject resultList = (SoapObject) result
				.getProperty("inviteUserResult");
		Object propertyValue = null;

		propertyValue = resultList.getProperty("rescode");

		
		if (!"anyType{}".equals(propertyValue)) {
			rescode = propertyValue.toString();
		}
		propertyValue = resultList.getProperty("error");
		if (!"anyType{}".equals(propertyValue)) {
			error = propertyValue.toString();
		}
		try{
		propertyValue = resultList.getProperty("inviteUserCount");
		if (!"anyType{}".equals(propertyValue)) {
			inviteUserCount = propertyValue.toString();
		}
		propertyValue = resultList.getProperty("inviteCode");
		if (!"anyType{}".equals(propertyValue)) {
			inviteCode = propertyValue.toString();
		}
		propertyValue = resultList.getProperty("expiredDate");
		if (!"anyType{}".equals(propertyValue)) {
			expiredDate = propertyValue.toString();
		}
		}catch(Exception e){
			
		}
	}

	public String getRescode() {
		return rescode;
	}

	public String getError() {
		return error;
	}

	public String getInviteUserCount() {
		return inviteUserCount;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

}
