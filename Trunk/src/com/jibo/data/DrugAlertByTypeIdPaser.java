package com.jibo.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.DrugAlertEntity;

/**
 * 用药安全最新20条数据.
 * 
 * @author simon
 * 
 */
public class DrugAlertByTypeIdPaser extends SoapDataPaser {

	private DrugAlertEntity entity;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		entity = new DrugAlertEntity();

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_DRUGALERTS_NEW_DATA);

		try {
			String[] Temp = new String[30];
			String regEx = "(?<==)[^;]+(?=;)";
			Pattern p = Pattern.compile(regEx);
			String date = detail.getProperty(0).toString();
			Matcher m = p.matcher(date);

			int test = 0;
			while (m.find()) {
				Temp[test] = new String(m.group());
				if (Temp[test].equals("anyType{}"))
					Temp[test] = "";
				test++;
			}
			entity.setId(Temp[0]);
			entity.setTypeId(Temp[1]);
			entity.setTitle(Temp[2]);
			entity.setTime(Temp[3]);
			entity.setSource(Temp[4]);
			entity.setContent(Temp[5]);
			entity.setProductNameCN(Temp[6]);
			entity.setCategory(Temp[7]);
			entity.setDate(Temp[8]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bSuccess = true;
	}

	public DrugAlertEntity getEntity() {
		return entity;
	}
}
