package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * 获取广告图片地址解析回调类
 * 
 * @author simon
 * 
 */
public class GetAdvertisingImagePaser extends SoapDataPaser {

	private String imagePath;
	private String category;
	private String adUrl;
	private String company;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_IMAGE_PATH);

		String date;
		PropertyInfo propertyInfo = new PropertyInfo();
		int i = 0;
		do {
			try {
				detail.getPropertyInfo(i, propertyInfo);
				date = detail.getProperty(i).toString();
				if (date.equals("anyType{}"))
					date = "";

				if (propertyInfo.name.equals("imagePath")) {
					imagePath = date;
				} else if (propertyInfo.name.equals("category")) {
					category = date;
				} else if (propertyInfo.name.equals("adUrl")) {
					adUrl = date;
				} 
				else if (propertyInfo.name.equals("company")) {
					company = date;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			i++;
		} while (date != null);
		bSuccess = true;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getCategory() {
		return category;
	}

	public String getAdUrl() {
		return adUrl;
	}

	public String getCompany() {
		return company;
	}

}
