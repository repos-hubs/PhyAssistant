package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
/**
 * @���ܣ������Ӻ�̨���ص�ͨ������ȡ���������Ϣ
 * @author Terry
 *
 */
public class GetForgetPwdParser extends SoapDataPaser {
	private boolean valid;//�ɹ��ı�־
	private String resMsg;//��Ӧ����Ϣ
	private String resStatuCode;//��Ӧ��״̬��
	

	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_PWD);

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
							valid = true;
						} else {
							resStatuCode = strValue;		
							String error = "error=";
							if (date.contains(error)) {
								this.resMsg = date.substring(
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

	public String getResmsg() {
		return resMsg;
	}

	public String getResCode() {
		return resStatuCode;
	}
}
