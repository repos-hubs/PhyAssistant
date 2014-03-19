package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.NewsEntity;

/**
 * 新闻列表解析类
 * 
 * @author simon
 * 
 */
public class NewsListPaser extends SoapDataPaser {

	// @Override
	// public void paser(SoapSerializationEnvelope response) {
	// list = new ArrayList<NewsEntity>();
	//
	// SoapObject result = (SoapObject) response.bodyIn;
	// SoapObject detail = (SoapObject) result
	// .getProperty(SoapRes.RESULT_PROPERTY_GET_NEWS_BYID_DESC);
	//
	// ;
	//
	// String date;
	// String[] Temp = new String[30];
	// String regEx = "(?<==)[^;]+(?=;)";
	// Pattern p = Pattern.compile(regEx);
	// int i = 0;
	// do {
	// try {
	// date = detail.getProperty(i).toString();
	// Matcher m = p.matcher(date);
	//
	// int test = 0;
	// while (m.find()) {
	// Temp[test] = new String(m.group());
	// if (Temp[test].equals("anyType{}"))
	// Temp[test] = "";
	// test++;
	// }
	// NewsEntity entity = new NewsEntity(Temp[0], Temp[1], Temp[2],
	// Temp[3], Temp[4], Temp[5], Temp[6], Temp[7]);
	// list.add(entity);
	// } catch (Exception e) {
	// e.printStackTrace();
	// break;
	// }
	// i++;
	// } while (date != null);
	// bSuccess = true;
	// }

	private String nullTag = "anyType{}";

	@Override
	public void paser(SoapSerializationEnvelope response) {

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_NEWS_BYID_DESC);
		list = new ArrayList<NewsEntity>();

		PropertyInfo propertyInfo = new PropertyInfo();

		String[] resultKey = new String[] { "ID", "Title", "Date", "Source",
				"DateTime", "typeID", "Content", "news_Source" };
		int resultLength = resultKey.length;

		for (int i = 0; i < detail.getPropertyCount(); i++) {
			SoapObject soapChilds = (SoapObject) detail.getProperty(i);
			NewsEntity entity = new NewsEntity();
			for (int j = 0; j < resultLength; j++) {
				String strValue = soapChilds.getProperty(resultKey[j])
						.toString();
				if ("".equals(strValue) || strValue.equals(nullTag))
					continue;
				if (resultKey[j].equals("ID")) {
					entity.setId(strValue);
				} else if (resultKey[j].equals("Title")) {
					entity.setTitle(strValue);
				} else if (resultKey[j].equals("Date")) {
					entity.setDate(strValue);
				} else if (resultKey[j].equals("Source")) {
					entity.setSource(strValue);
				} else if (resultKey[j].equals("DateTime")) {
					entity.setTime(strValue);
				} else if (resultKey[j].equals("typeID")) {
					entity.setTypeID(strValue);
				} else if (resultKey[j].equals("Content")) {
					entity.setContent(strValue);
				} else if (resultKey[j].equals("news_Source")) {
					entity.setNewSource(strValue);
				}
			}
			list.add(entity);
		}
		bSuccess = true;
	}

	public ArrayList<NewsEntity> list;

	public ArrayList<NewsEntity> getList() {
		return list;
	}

}