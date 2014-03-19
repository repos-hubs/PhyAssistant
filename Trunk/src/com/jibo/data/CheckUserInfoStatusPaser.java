package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.LoginEntity;

/**
 * 检查用户信息验证状态解析器
 * 
 * @author simon
 * 
 */
public class CheckUserInfoStatusPaser extends SoapDataPaser {

	private boolean valid;
	private String errmsg;
	private String code;
	
	
	private String licenseNumber;
	private String checkStatus;
	private String checkInfo;

	private String nullTag = "anyType{}";

	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_CHECK_USER_INFO_STATUS);

		PropertyInfo propertyInfo = new PropertyInfo();

		for (int i = 0; i < detail.getPropertyCount(); i++) {
			SoapObject soapChilds = (SoapObject) detail.getProperty(i);
			if (soapChilds.toString().equals(nullTag))
				continue;
			detail.getPropertyInfo(i, propertyInfo);
			if (propertyInfo.name.equals("rescode")) {
				code = soapChilds.getProperty("rescode").toString();
				if (code.equals("200")) {
					this.valid = true;
				} else if (code.equals("202")) {
					this.valid = true;
					this.errmsg = soapChilds.getProperty("error").toString()
							.equals(nullTag) ? "" : soapChilds.getProperty(
							"error").toString();
				} else {
					this.errmsg = soapChilds.getProperty("error").toString()
							.equals(nullTag) ? "" : soapChilds.getProperty(
							"error").toString();
					break;
				}
			} else if (propertyInfo.name.equals("customerinfo")) {
				licenseNumber = soapChilds.getProperty("LicenseNumber").toString().equals(nullTag) ? "":soapChilds.getProperty("LicenseNumber").toString();
				checkStatus = soapChilds.getProperty("checkStatus").toString().equals(nullTag) ? "":soapChilds.getProperty("checkStatus").toString();
				checkInfo = soapChilds.getProperty("checkInfo").toString().equals(nullTag) ? "":soapChilds.getProperty("checkInfo").toString();
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

	public String getCode() {
		return code;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public String getCheckInfo() {
		return checkInfo;
	}
	
	
	


}