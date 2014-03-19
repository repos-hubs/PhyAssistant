package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * 检查用户名是否存在
 * 
 * @author simon
 * 
 */
public class RegistrationCheckTheUserIsValidPaser extends SoapDataPaser {

	private String password;
	private String region;
	private String department;

	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_CHECK_USERNAME_ISVALID);

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
					password = date;
				} else if (propertyInfo.name.equals("s_region")) {
					region = date;
				} else if (propertyInfo.name.equals("department")) {
					department = date;
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			i++;
		} while (date != null);
		bSuccess = true;
	}

	public String getPassword() {
		return password;
	}
	public String getRegion() {
		return region;
	}
	public String getDepartment() {
		return department;
	}

}
