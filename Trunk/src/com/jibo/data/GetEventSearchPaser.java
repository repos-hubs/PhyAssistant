package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import com.jibo.data.entity.RelatedBeans;


public class GetEventSearchPaser extends SoapDataPaser{
	public ArrayList<RelatedBeans> al=new ArrayList<RelatedBeans>();
	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
		String[] Temp=new String[30];
		
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("AdvancedRetrieveEvents_newResult");
		
		int j = 0;
		do{
			try{
				date = detail.getProperty(j).toString();

				Matcher m = p.matcher(date);
				
				int test=0;
				while(m.find()){
					Temp[test] = new String(m.group());
					if(Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
				}
				RelatedBeans advancedretrieveevent = new RelatedBeans();
				advancedretrieveevent.setID(Temp[0]);
				advancedretrieveevent.setName(Temp[1]);
				advancedretrieveevent.setPlace(Temp[2]);
				advancedretrieveevent.setEventDate(Temp[3]);
				al.add(advancedretrieveevent);
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			j++;
		}while(date != null);
	}

}
