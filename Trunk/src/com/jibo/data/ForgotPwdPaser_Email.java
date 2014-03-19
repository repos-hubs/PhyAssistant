package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * email∑Ω Ω√‹¬Î÷ÿ÷√
 * @author will
 * 
 */
public class ForgotPwdPaser_Email extends SoapDataPaser {

	private boolean valid;
	private String errmsg;
	private String errorCode;

	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_FORGOT_PWD_EMAIL);

		
		String date;
		PropertyInfo propertyInfo;
		String strValue;

		propertyInfo = new PropertyInfo();
		int i = 0;
		do {
			try {
				detail.getPropertyInfo(i, propertyInfo);
				date = detail.getProperty(i).toString();
				if (date.equals("anyType{}"))
					date = "";

				
				if(propertyInfo.name.equals("rescode")) {
					errorCode = date;
				}
				if(propertyInfo.name.equals("error")){
					errmsg = date;
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			i++;
		} while (date != null);

		bSuccess = true;
	}

	public boolean isSuccess() {
		return valid;
	}

	public String getErrorMsg() {
		return errmsg;
	}
	
	public String getErrorCode(){
		return errorCode;
	}

}
