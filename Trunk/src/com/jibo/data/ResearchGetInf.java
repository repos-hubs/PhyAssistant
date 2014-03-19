package com.jibo.data;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.jibo.data.entity.GetArticleInfoBeans;
import com.jibo.data.entity.RelatedDrugsBeans;


public class ResearchGetInf extends SoapDataPaser{
	public GetArticleInfoBeans getarticleinfo;
	public RelatedDrugsBeans relateddrugs;
	public ArrayList<RelatedDrugsBeans> allRelDrg=new ArrayList<RelatedDrugsBeans>();
	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
		PropertyInfo propertyInfo;
		String EndFlag="};";
		//String[] Temp=new String[30];
		
		//String regEx="(?<==)[^;]+(?=;)";
		//Pattern p = Pattern.compile(regEx);
		propertyInfo = new PropertyInfo();

		int c= 0;
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("GetArticleInfoResult");
		getarticleinfo = new GetArticleInfoBeans();
		do{
			try{
				detail.getPropertyInfo(c, propertyInfo);
				date = detail.getProperty(c).toString();
				if(date.equals("anyType{}"))
					date = "";

				
		        if(propertyInfo.name.equals("ID"))
				{
		        	getarticleinfo.setID(date);
		        	Log.e("id", date);
				}
				else if(propertyInfo.name.equals("Title"))
				{
					getarticleinfo.setTitle(date);
		        	Log.e("Title", date);
				}
				else if(propertyInfo.name.equals("Authors"))
				{
					getarticleinfo.setAuthors(date);
		        	Log.e("Authors", date);
				}
				else if(propertyInfo.name.equals("JournalName"))
				{
					getarticleinfo.setJournalName(date);
		        	Log.e("JournalName", date);
				}
				else if(propertyInfo.name.equals("AuthorEntities"))
				{

					getarticleinfo.setAuthorEntities(date);
		        	Log.e("AuthorEntities", date);
				}
				else if(propertyInfo.name.equals("DateAndVolume"))
				{

					getarticleinfo.setDateAndVolume(date);
		        	Log.e("DateAndVolume", date);
				}
				else if(propertyInfo.name.equals("Abstract"))
				{

					getarticleinfo.setAbstract(date);
		        	Log.e("Abstract", date);
				}
				else if(propertyInfo.name.equals("DrugsCount"))
				{

					getarticleinfo.setDrugsCount(date);
		        	Log.e("DrugsCount", date);
				}
				else if(propertyInfo.name.equals("RelatedDrugs"))
				{
					Integer ddd = new Integer(Integer.parseInt(getarticleinfo.getDrugsCount()));
					int iLayer = ddd.intValue();
//					relateddrugs.iRecordLength = iLayer;
					
					if(iLayer > 0)
					{
						for(int j = 0; j < iLayer; j++)
						{
							relateddrugs = new RelatedDrugsBeans();
							Field[] fields = relateddrugs.getClass().getDeclaredFields();  
							for (int i = 0; i < fields.length; i++) {            
								fields[i].setAccessible(true); 
		
								//detect String            
//								if (fields[i].getType().equals(String[].class)) {                
									String tag = fields[i].getName() + "=";                
									if(date.contains(tag)){                    
										String strValue = date.substring(date.indexOf(tag)+tag.length(), date.indexOf(";", date.indexOf(tag)));                    
										if(strValue.length()!=0){
											//fields[i].set(Temp, strValue);
											if(fields[i].getName().equals("ID"))
											{
									        	Log.e("IDdg",strValue);
												relateddrugs.setID(strValue);
											}
											else if(fields[i].getName().equals("Name_EN"))
											{
												relateddrugs.setName_EN(strValue);
												Log.e("Name_EN", strValue);
											}
											else if(fields[i].getName().equals("Name_CN"))
											{
												relateddrugs.setName_CN(strValue);
												Log.e("Name_CN", strValue);
											}
											else if(fields[i].getName().equals("ATC"))
											{
												relateddrugs.setATC(strValue);
												Log.e("ATC", strValue);
											}

										}
										
									}            
//								}
							}
							
							allRelDrg.add(relateddrugs);
							date = date.substring(date.indexOf(EndFlag)+EndFlag.length());
						}
					}
				}
				else if(propertyInfo.name.equals("Source"))
				{

					getarticleinfo.setSource(date);
				}
				else if(propertyInfo.name.equals("KeyWords"))
				{
	
					getarticleinfo.setKeyWords(date);
				}
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			c++;
		}while(date != null);
	}

}
