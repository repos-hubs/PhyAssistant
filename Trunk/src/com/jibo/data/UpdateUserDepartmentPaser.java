package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.LoginEntity;

/**
 * 修改科室信息response解析
 * 
 * @author simon
 * 
 */
public class UpdateUserDepartmentPaser extends SoapDataPaser {

	private boolean valid;
	private String errmsg;
	private LoginEntity login;

	private String nullTag = "anyType{}";
	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_UPDATE_USER_DEPARTMENT);
		login = new LoginEntity();

		PropertyInfo propertyInfo = new PropertyInfo();

		for (int i = 0; i < detail.getPropertyCount(); i++) {
			SoapObject soapChilds = (SoapObject) detail.getProperty(i);
			if (soapChilds.toString().equals(nullTag))
				continue;
			detail.getPropertyInfo(i, propertyInfo);
			if (propertyInfo.name.equals("rescode")) {
				if (soapChilds.getProperty("rescode").toString().equals("200")) {
					this.valid = true;
				} else {
					this.errmsg = soapChilds.getProperty("error").toString()
							.equals(nullTag) ? "" : soapChilds.getProperty(
							"error").toString();
					break;
				}
			} else if (propertyInfo.name.equals("customerinfo")) {
				String[] resultKey = new String[] { "gbUserName", "UserName",
						"LicenseNumber", "Email", "ContactNumber",
						"HospitalRegion", "HospitalCity", "HospitalName",
						"bigDepartments", "Department", "professional_title",
						"inviteCode", "companyName", "imagePath", "doctorId",
						"customerId" };

				int resultLength = resultKey.length;
				for (int j = 0; j < resultLength; j++) {
					String strValue = soapChilds.getProperty(resultKey[j])
							.toString();
					if ("".equals(strValue) || strValue.equals(nullTag))
						continue;

					if (resultKey[j].equals("gbUserName")) {
						login.setGbUserName(strValue);
					} else if (resultKey[j].equals("UserName")) {
						login.setUserName(strValue);
					} else if (resultKey[j].equals("LicenseNumber")) {
						login.setLicenseNumber(strValue);
					} else if (resultKey[j].equals("Email")) {
						login.setEmail(strValue);
					} else if (resultKey[j].equals("ContactNumber")) {
						login.setContactNumber(strValue);
					} else if (resultKey[j].equals("HospitalRegion")) {
						login.setHospitalRegion(strValue);
					} else if (resultKey[j].equals("HospitalCity")) {
						login.setHospitalCity(strValue);
					} else if (resultKey[j].equals("HospitalName")) {
						login.setHospitalName(strValue);
					} else if (resultKey[j].equals("bigDepartments")) {
						login.setBigDepartments(strValue);
					} else if (resultKey[j].equals("Department")) {
						login.setDepartment(strValue);
					} else if (resultKey[j].equals("professional_title")) {
						login.setProfessional_title(strValue);
					} else if (resultKey[j].equals("inviteCode")) {
						login.setInviteCode(strValue);
					} else if (resultKey[j].equals("companyName")) {
						login.setCompanyName(strValue);
					} else if (resultKey[j].equals("imagePath")) {
						login.setImagePath(strValue);
					} else if (resultKey[j].equals("doctorId")) {
						login.setDoctorId(strValue);
					} else if (resultKey[j].equals("customerId")) {
						login.setCustomerId(strValue);
					}

				}

			}
		}
		bSuccess = true;
	}

	public boolean isSuccess() {
		return valid;
	}

	public String getErrorMsg() {
		return errmsg;
	}

	public LoginEntity getLoginEntity() {
		return login;
	}

}
