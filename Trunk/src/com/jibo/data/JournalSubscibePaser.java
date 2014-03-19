package com.jibo.data;

import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.base.src.EntityObj;
import com.jibo.common.SoapRes;
import com.jibo.data.entity.JournalSubscribeEntity;

/**
 * ¶©ÔÄ
 * @description 
 * @author will
 * @create 2013-3-20 ÏÂÎç5:01:38
 */
public class JournalSubscibePaser extends SoapDataPaser {
	public JournalSubscribeEntity entity;
	
	@Override
	public List<EntityObj> getObjs() {
		return super.getObjs();
	}


	@Override
	public void paser(SoapSerializationEnvelope response) {

		entity = new JournalSubscribeEntity();

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_JOURNAL_SUBSCIBE);

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
					entity.resCode = date;
				} else if (propertyInfo.name.equals("ErrorMsg")) {
					entity.errorMsg = date;
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
