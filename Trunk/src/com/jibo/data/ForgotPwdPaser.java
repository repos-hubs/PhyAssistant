package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * √‹¬Î÷ÿ÷√
 * @author simon
 * 
 */
public class ForgotPwdPaser extends SoapDataPaser {

	private boolean valid;
	private String errmsg;
	private String errorCode;

	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_FORGOT_PWD);

		
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

				
				if (propertyInfo.name.equals("rescode")) {
					String code = "rescode=";
					if (date.contains(code)) {
						strValue = date.substring(
								date.indexOf(code) + code.length(),
								date.indexOf(";", date.indexOf(code)));
						if (strValue.equals("200")) {
							this.valid = true;
						} else {
							errorCode = strValue;		
							String error = "error=";
							if (date.contains(error)) {
								this.errmsg = date.substring(
										date.indexOf(error) + error.length(),
										date.indexOf(";", date.indexOf(error)));
								}
							break;
						}
					}
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
