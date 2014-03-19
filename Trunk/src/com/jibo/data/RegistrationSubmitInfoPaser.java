package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * 提交注册信息，回调解析接口
 * 
 * @author simon
 * 
 */
public class RegistrationSubmitInfoPaser extends SoapDataPaser {

	private boolean isValid;
	private String doctorId;
	private String userId;
	private boolean isRegistrationOk;
	private String company;

	private boolean inviteCodeValid;

	private  String imagePath;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		System.out.println("   RegistrationSubmitInfoPaser  paser    ");
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_REGISTRATION_SUBMIT_INFO);

		String date;
		PropertyInfo propertyInfo = new PropertyInfo();
		int i = 0;
		do {
			try {
				detail.getPropertyInfo(i, propertyInfo);
				date = detail.getProperty(i).toString();
				if (date.equals("anyType{}"))
					date = "";

				if (propertyInfo.name.equals("IsValid")) {
					if (date.equals("false"))
						isValid = false;
					else
						isValid = true;
				} else if (propertyInfo.name.equals("DoctorId")) {
					doctorId = date;
				} else if (propertyInfo.name.equals("UserId")) {
					userId = date;
				} else if (propertyInfo.name.equals("CompanyName")) {
					company = date;
				}else if (propertyInfo.name.equals("imagePath")) {
					imagePath = date;
				}
				else if (propertyInfo.name.equals("InviteCodeValid")) {
					if (date.equals("False"))
						inviteCodeValid = false;
					else
						inviteCodeValid = true;
				} else if (propertyInfo.name.equals("IsRegisterOk")) {
					if (date.equals("faile"))
						isRegistrationOk = false;
					else
						isRegistrationOk = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			i++;
		} while (date != null);
		bSuccess = true;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public boolean getIsValid() {
		return isValid;
	}

	public boolean isSuccess() {
		return isRegistrationOk;
	}

	public String getUserId() {
		return userId;
	}
	
	public String getImagePath(){
		return imagePath;
	}

	public String getCompany() {
		return company;
	}

	public boolean getInviteCodeValid() {
		return inviteCodeValid;
	}

}
