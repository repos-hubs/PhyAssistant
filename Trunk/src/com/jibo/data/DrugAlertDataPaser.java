package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.DrugAlertEntity;

/**
 * 用药安全加载更多，每次10条
 * @author simon
 *
 */
public class DrugAlertDataPaser extends SoapDataPaser {

	private ArrayList<DrugAlertEntity> list;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		list = new ArrayList<DrugAlertEntity>();

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_DRUGALERTS_NEW_DATA);

		String date;
		String[] Temp = new String[30];
		String regEx = "(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		int i = 0;
		do {
			try {
				date = detail.getProperty(i).toString();
				Matcher m = p.matcher(date);

				int test = 0;
				while (m.find()) {
					Temp[test] = new String(m.group());
					if (Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
				}
				DrugAlertEntity entity = new DrugAlertEntity();
				entity.setId(Temp[0]);
				entity.setTypeId(Temp[1]);
				entity.setTitle(Temp[2]);
				entity.setTime(Temp[3]);
				entity.setSource(Temp[4]);
				entity.setContent(Temp[5]);
				entity.setProductNameCN(Temp[6]);
				entity.setCategory(Temp[7]);
				entity.setDate(Temp[8]);
				list.add(entity);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			i++;
		} while (date != null);

		bSuccess = true;
	}

	public ArrayList<DrugAlertEntity> getList() {
		return list;
	}
}
