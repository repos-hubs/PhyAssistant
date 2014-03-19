package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.base.src.EntityUtil;
import com.jibo.common.SoapRes;
import com.jibo.data.entity.NewsCategoriesEntity;

/**
 * 获取新闻类别 的 所有子类别信息
 * 
 * @author simon
 * 
 */
public class NewsGetCategoriesByBigCategoryPaser extends SoapDataPaser {

	private ArrayList<NewsCategoriesEntity> list;
	
	@Override
	public void paser(SoapSerializationEnvelope response) {
		list = new ArrayList<NewsCategoriesEntity>();

		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty(SoapRes.RESULT_PROPERTY_GET_NEWS_CATEGORIES_BY_BIGCATEGROY);

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
				NewsCategoriesEntity entity = new NewsCategoriesEntity(Temp[1],
						Temp[2], Temp[3], Temp[0]);
				rslt.add(EntityUtil.convert(entity));
				list.add(entity);
			} catch (Exception e) {
				
				break;
			}
			i++;
		} while (date != null);

		bSuccess = true;
	}

	public ArrayList<NewsCategoriesEntity> getList() {
		return list;
	}

}
