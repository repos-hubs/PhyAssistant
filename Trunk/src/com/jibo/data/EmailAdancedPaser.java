package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * ” º˛Ω‚Œˆ¿‡
 * 
 * @author simon
 * 
 */
public class EmailAdancedPaser extends SoapDataPaser {

	private String flag;

	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_SEND_MAIL);

		String date;
		PropertyInfo propertyInfo;
		propertyInfo = new PropertyInfo();
		int i = 0;
		do {
			try {
				detail.getPropertyInfo(i, propertyInfo);
				date = detail.getProperty(i).toString();
				if (date.equals("anyType{}"))
					date = "";
				else if (propertyInfo.name.equals("sendResult")) {
					flag = date;
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
		return flag!=null&&flag.equals("successful")?true:false;
	}

}
