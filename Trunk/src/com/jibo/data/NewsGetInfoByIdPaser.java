package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.NewsEntity;

/**
 * 根据新闻id获取详细内容
 * 
 * @author simon
 * 
 */
public class NewsGetInfoByIdPaser extends SoapDataPaser {

	private NewsEntity entity;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		
		entity = new NewsEntity();

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_NEWSDETAIL_BY_ID);
		
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

				if (propertyInfo.name.equals("ID")) {
					entity.setId(date);
				} else if (propertyInfo.name.equals("Title")) {
					entity.setTitle(date);
				} else if (propertyInfo.name.equals("Date")) {
					entity.setDate(date);
				} else if (propertyInfo.name.equals("Source")) {
					entity.setSource(date);
				} else if (propertyInfo.name.equals("Content")) {
					entity.setContent(date);
				} else if (propertyInfo.name.equals("typeID")) {
					entity.setTypeID(date);
				}else if (propertyInfo.name.equals("news_Source")) {
					entity.setNewSource(date);
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			i++;
		} while (date != null);
		bSuccess = true;
	}

	public NewsEntity getEntity() {
		return entity;
	}
}
