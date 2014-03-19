package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.LoginEntity;

/**
 * 第三方登陆response解析
 * 
 * @author simon
 * 
 */
public class WeiboLoginPaser extends SoapDataPaser {

	private String errmsg;
	private LoginEntity login;
	private boolean isBindSuccess;
	private boolean isGetUserInfoSuccess;
	private String placeName;

	private String nullTag = "anyType{}";

	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_WEIBO_LOGIN);
		login = new LoginEntity();

		PropertyInfo propertyInfo = new PropertyInfo();

		for (int i = 0; i < detail.getPropertyCount(); i++) {
			SoapObject soapChilds = (SoapObject) detail.getProperty(i);
			if (soapChilds.toString().equals(nullTag))
				continue;
			detail.getPropertyInfo(i, propertyInfo);
			if (propertyInfo.name.equals("rescode")) {
				String code = soapChilds.getProperty("rescode").toString();
				if (code.equals("201")) {
					this.isBindSuccess = true;
					placeName = soapChilds.getProperty("user_address")
							.toString().equals(nullTag) ? "" : soapChilds
							.getProperty("user_address").toString();
				} else if (code.equals("200")) {
					this.isGetUserInfoSuccess = true;
				} else {
					this.errmsg = soapChilds.getProperty("error").toString()
							.equals(nullTag) ? "" : soapChilds.getProperty(
							"error").toString();
					break;
				}
			} else if (propertyInfo.name.equals("customerinfo")) {
				login.setGbUserName(soapChilds.getProperty("gbUserName").toString().equals(nullTag) ? "":soapChilds.getProperty("gbUserName").toString());
				login.setUserName(soapChilds.getProperty("UserName").toString().equals(nullTag) ? "":soapChilds.getProperty("UserName").toString());
				login.setLicenseNumber(soapChilds.getProperty("LicenseNumber").toString().equals(nullTag) ? "":soapChilds.getProperty("LicenseNumber").toString());
				login.setEmail(soapChilds.getProperty("Email").toString().equals(nullTag) ? "":soapChilds.getProperty("Email").toString());
				login.setContactNumber(soapChilds.getProperty("ContactNumber").toString().equals(nullTag) ? "":soapChilds.getProperty("ContactNumber").toString());
				login.setHospitalRegion(soapChilds.getProperty("HospitalRegion").toString().equals(nullTag) ? "":soapChilds.getProperty("HospitalRegion").toString());
				login.setHospitalCity(soapChilds.getProperty("HospitalCity").toString().equals(nullTag) ? "":soapChilds.getProperty("HospitalCity").toString());
				login.setHospitalName(soapChilds.getProperty("HospitalName").toString().equals(nullTag) ? "":soapChilds.getProperty("HospitalName").toString());
				login.setBigDepartments(soapChilds.getProperty("bigDepartments").toString().equals(nullTag) ? "":soapChilds.getProperty("bigDepartments").toString());
				login.setDepartment(soapChilds.getProperty("Department").toString().equals(nullTag) ? "":soapChilds.getProperty("Department").toString());
				login.setProfessional_title(soapChilds.getProperty("professional_title").toString().equals(nullTag) ? "":soapChilds.getProperty("professional_title").toString());
				login.setInviteCode(soapChilds.getProperty("inviteCode").toString().equals(nullTag) ? "":soapChilds.getProperty("inviteCode").toString());
				login.setCompanyName(soapChilds.getProperty("companyName").toString().equals(nullTag) ? "":soapChilds.getProperty("companyName").toString());
				login.setImagePath(soapChilds.getProperty("imagePath").toString().equals(nullTag) ? "":soapChilds.getProperty("imagePath").toString());
				login.setDoctorId(soapChilds.getProperty("doctorId").toString().equals(nullTag) ? "":soapChilds.getProperty("doctorId").toString());
				login.setCustomerId(soapChilds.getProperty("customerId").toString().equals(nullTag) ? "":soapChilds.getProperty("customerId").toString());
				login.setInviteCodeExpiredDate(soapChilds.getProperty("ExpiredDate").toString().equals(nullTag) ? "":soapChilds.getProperty("ExpiredDate").toString());
			}
		}
		bSuccess = true;
	}

	public String getErrorMsg() {
		return errmsg;
	}

	public LoginEntity getLoginEntity() {
		return login;
	}

	public boolean isBindSuccess() {
		return isBindSuccess;
	}

	public boolean isGetUserInfoSuccess() {
		return isGetUserInfoSuccess;
	}

	public String getPlaceName() {
		return placeName;
	}

}
