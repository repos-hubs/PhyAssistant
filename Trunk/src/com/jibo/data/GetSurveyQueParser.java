package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.DrugSurvey;

import android.util.Log;

public class GetSurveyQueParser extends SoapDataPaser{
	public ArrayList<DrugSurvey> list=new ArrayList<DrugSurvey>();
	public DrugSurvey drgSurvey;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
		String[] Temp=new String[30];
		
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("getQuestionNewResult");
		int j = 0;
		do{
			try{
				drgSurvey=new DrugSurvey();
				date = detail.getProperty(j).toString();

				Matcher m = p.matcher(date);
				
				int test=0;
				while(m.find()){
					Temp[test] = new String(m.group());
					if(Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
				}
				Log.e("temp0",Temp[0]);
				Log.e("temp1",Temp[1]);
				Log.e("temp2",Temp[2]);
				Log.e("temp3",Temp[3]);
				Log.e("temp4",Temp[4]);
				drgSurvey.setQid(Temp[0]);
				drgSurvey.setQversion(Temp[1]);
				drgSurvey.setQtopic(Temp[2]);
				drgSurvey.setQtitle(Temp[3]);
				drgSurvey.setQcontent(Temp[4]);
				drgSurvey.setQtype(Temp[5]);
				drgSurvey.setAid(Temp[6]);
				drgSurvey.setAconttent(Temp[7]);
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			list.add(drgSurvey);
			j++;
		}while(date != null);
	}

}
