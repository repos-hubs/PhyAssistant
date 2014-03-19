package com.jibo.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.jibo.common.Constant;
import com.jibo.data.entity.RelatedBeans;

public class EventListParser extends SoapDataPaser {

	public ArrayList<RelatedBeans> list=new ArrayList<RelatedBeans>();
	public RelatedBeans relatedBean;
	
	@Override
	public void paser(SoapSerializationEnvelope response) {
		String date;
		String[] Temp=new String[30];
		String EndFlag="};";
		String strValue;
		String regEx="(?<==)([^;]+?)(?=;)";//(?<==)([^;]+?)(?=;)
		Pattern p = Pattern.compile(regEx);
		relatedBean=new RelatedBeans();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("RetrieveEventsNewResult");

		PropertyInfo propertyInfo=new PropertyInfo();
		int j=0;
		do{
			try{				
				detail.getPropertyInfo(j, propertyInfo);
				date = detail.getProperty(j).toString();
				Field[] fields = relatedBean.getClass().getDeclaredFields();  
				Log.e("RelatedBeans.class.getClass().getDeclaredFields()", fields[0].getName());
				RelatedBeans relatedBeans = new RelatedBeans();
				for (int i = 0; i < fields.length; i++) { 
//					Log.e("fields[i].getName()",fields[i].getName());

					fields[i].setAccessible(true);             
							//detect String            
//					if (fields[i].getType().equals(String[].class)) {                
						String tag = fields[i].getName() + "=";                
						if(date.contains(tag)){
						if(tag.contains("VersionDate"))
							strValue = date.substring(date.indexOf(tag)+tag.length(), date.indexOf("; }", date.indexOf(tag)));                    
						else
							strValue = date.substring(date.indexOf(tag)+tag.length(), date.indexOf(";", date.indexOf(tag)));                    
									
						if(strValue.length()!=0){
							//fields[i].set(Temp, strValue);
						if(fields[i].getName().equals("ID")&&!strValue.equals("anyType{}"))
							relatedBeans.setID(strValue);
						if(fields[i].getName().equals("Name")&&!strValue.equals("anyType{}"))
						{
							relatedBeans.setName(strValue);
							Log.e("Name",strValue);
							
						}
						if(fields[i].getName().equals("Place")&&!strValue.equals("anyType{}"))
							relatedBeans.setPlace(strValue);
						if(fields[i].getName().equals("EventDate")&&!strValue.equals("anyType{}"))
							relatedBeans.setEventDate(strValue);
						if(fields[i].getName().equals("Organizer")&&!strValue.equals("anyType{}"))  // prime added for news localization 2011-6-28.
							relatedBeans.setOrganizer(strValue);
						if(fields[i].getName().equals("Tel")&&!strValue.equals("anyType{}")) // prime
							relatedBeans.setTel(strValue);
						if(fields[i].getName().equals("Fax")&&!strValue.equals("anyType{}")) // prime
							relatedBeans.setFax(strValue);
						if(fields[i].getName().equals("Email")&&!strValue.equals("anyType{}")) // prime
						{
							relatedBeans.setEmail(strValue);
							Log.e("Email",strValue);
						}
						if(fields[i].getName().equals("Website")&&!strValue.equals("anyType{}")) // prime
						{
							relatedBeans.setWebsite(strValue);
							Log.e("Website",strValue);
						}
						if(fields[i].getName().equals("Introduction")&&!strValue.equals("anyType{}")) // prime
							
							{
							relatedBeans.setIntroduction(strValue);
							Log.e("Introduction", strValue);
							}
						if(fields[i].getName().equals("VersionDate")&&!strValue.equals("anyType{}"))
						{
							relatedBeans.setVersionDate(strValue);
							Log.e("VersionDate", strValue);
						}

					

						
//								}
							}
						}
					}
				list.add(relatedBeans);
					date = date.substring(date.indexOf(EndFlag)+EndFlag.length());
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			j++;
		}while(date != null);
	}
	
	public ArrayList<RelatedBeans> getList() {
		return list;
	}

}
