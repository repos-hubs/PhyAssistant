package com.jibo.data;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.app.invite.ContactInfo;
import com.jibo.util.Logs;

public class GetFriendsPaser extends SoapDataPaser {
	private String rescode = "";
	private String error = "";
	private List<ContactInfo> contactInfos = new ArrayList<ContactInfo>(0);

	public String getRescode() {
		return rescode;
	}

	public String getError() {
		return error;
	}

	public List<ContactInfo> getContactInfo() {
		return contactInfos;
	}

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		contactInfos.clear();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject resultList = (SoapObject) result
				.getProperty("getInvitedUserResult");
		Object propertyValue = null;
		propertyValue = resultList.getProperty("rescode");
		if (!"anyType{}".equals(propertyValue)) {
			rescode = propertyValue.toString();
		}
		propertyValue = resultList.getProperty("error");
		if (!"anyType{}".equals(propertyValue)) {
			error = propertyValue.toString();
		}
		if (!rescode.equals("200")) {
			return;
		}
		SoapObject userList = ((SoapObject) resultList.getProperty("userList"));
		PropertyInfo propertyInfo = new PropertyInfo();
		
		SoapObject inviteUserInfoDetail;
		ContactInfo contactInfo = null;
		for(int i = 0;i<userList.getPropertyCount();i++){
			inviteUserInfoDetail = (SoapObject)userList.getProperty(i);
			contactInfo = new ContactInfo();
			propertyValue = inviteUserInfoDetail.getProperty("name");
			if (!"anyType{}".equals(propertyValue)) {
				contactInfo.name = propertyValue.toString();
			}
			propertyValue = inviteUserInfoDetail.getProperty("email");
			if (!"anyType{}".equals(propertyValue)) {
				contactInfo.email = propertyValue.toString();
			}
			propertyValue = inviteUserInfoDetail.getProperty("phone");
			if (!"anyType{}".equals(propertyValue)) {
				contactInfo.phone = propertyValue.toString();
			}
			Logs.i(""+contactInfo);
			this.contactInfos.add(contactInfo);

		}
	}
}