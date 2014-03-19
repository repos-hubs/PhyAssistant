package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * 新闻类别检查——回调解析工具类
 * @author simon
 *
 */
public class NewsCheckCategoriesPaser extends SoapDataPaser {

	private boolean update;

	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_CHECK_NEWS_CATEGORIES);

		String date;
		PropertyInfo propertyInfo = new PropertyInfo();
		int i = 0;	
		do{
			try{
				detail.getPropertyInfo(i, propertyInfo);
				date = detail.getProperty(i).toString();
				if(date.equals("anyType{}"))
					date = "";
				if(propertyInfo.name.equals("categoryFlagValue"))
				{
					update = Boolean.parseBoolean(date);
				}
			}catch (Exception e){
				
				break;
			}
			i++;
		}while(date != null);
		bSuccess = true;
	}
	
	public boolean isUpdate(){
		return update;
	}

}
