package com.jibo.data;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * ÐÞ¸ÄÃÜÂë
 * @author simon
 *
 */
public class UpdatePasswordPaser extends SoapDataPaser {


	private boolean isSuccess;

	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		Object detail = (Object) result
				.getProperty(SoapRes.RESULT_PROPERTY_UPDATE_PASSWORD);
		
		
		if(detail.toString().equals("true"))
			isSuccess = true;
		else
			isSuccess = false;
		bSuccess = true;
	}
	
	public boolean getIsSuccess(){
		return isSuccess;
	}

}
