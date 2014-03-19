package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

public class GetgoogleParser extends SoapDataPaser{
	public ArrayList<String> list=new ArrayList<String>();
	@Override
	public void paser(SoapSerializationEnvelope response) {
		String date;
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("DrugInfoTranslationResult");
		int j = 0;
        Log.e("datedate", detail.toString()+"1");
		do{
			try{
				date = detail.getProperty(j).toString();
                Log.e("datedate", date+"1");
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			if(date.equals("anyType{}"))
				date = "";
			list.add(date);
			j++;
		}while(j<detail.getPropertyCount());
	}

}
