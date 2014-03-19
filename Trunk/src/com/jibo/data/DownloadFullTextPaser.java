package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * 全文下载请求链接
 * @author will
 * @create 2013-6-8 下午6:33:09
 */
public class DownloadFullTextPaser extends SoapDataPaser {
	public String url;
	public String resCode;
	public static final String ERROR_CODE_NORES = "504";
	public static final String SUCCESS_CODE = "200";
	
	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_DOWNLOAD_FULLTEXT);

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

				if (propertyInfo.name.equals("ResCode")) {
					resCode = date;
				} else if (propertyInfo.name.equals("ReturnValue")) {
					url = date;
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			i++;
		} while (date != null);
		bSuccess = true;
	}

}
