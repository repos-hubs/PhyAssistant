package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.ResearchCategoryBean;

import android.util.Log;

public class ResearchCheckParser extends SoapDataPaser {
	public ArrayList<ResearchCategoryBean> list=new ArrayList<ResearchCategoryBean>();
	public ResearchCategoryBean relatedBean;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
		String[] Temp=new String[30];
		
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("ReKwCountResult");
		int j = 0;
		do{
			try{
				date = detail.getProperty(j).toString();
				relatedBean=new ResearchCategoryBean();
				Matcher m = p.matcher(date);
				
				int test=0;
				while(m.find()){
					Temp[test] = new String(m.group());
					if(Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
				}
				relatedBean.setTa_id(Temp[0]);
//				Log.e("Temp[0]", Temp[0]);
				relatedBean.setKw_id(Temp[1]);
//				Log.e("Temp[1]", Temp[1]);
				relatedBean.setKw(Temp[2]);
//				Log.e("Temp[2]", Temp[2]);
				relatedBean.setKwCount(Temp[3]);
//				Log.e("Temp[3]", Temp[3]);
				list.add(relatedBean);
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			j++;
		}while(date != null);
	}

}
