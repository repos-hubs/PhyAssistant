package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.DrugAlertDetailEntity;

/**
 * 用药安全详细内容paser，1个typeId对应多个detail
 * @author simon
 *
 */
public class DrugAlertDetailListPaser extends SoapDataPaser {

	private ArrayList<DrugAlertDetailEntity> list;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		list = new ArrayList<DrugAlertDetailEntity>();

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_DETAIL);

		String regEx = "(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		String data;
		String[] Temp = new String[30];
		int i = 0;
		do {
			try {
				data = detail.getProperty(i).toString();
				Matcher m = p.matcher(data);
				int test = 0;
				while (m.find()) {
					Temp[test] = new String(m.group());
					if (Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
				}
				list.add(new DrugAlertDetailEntity(Temp[0],Temp[1],Temp[2],Temp[3]));
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			i++;
		} while (data != null);

		bSuccess = true;
	}

	public ArrayList<DrugAlertDetailEntity> getList() {
		return list;
	}
}
