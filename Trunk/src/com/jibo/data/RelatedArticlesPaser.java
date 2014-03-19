package com.jibo.data;

import java.lang.reflect.Field;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.ArticleDetailsEntity;
import com.jibo.data.entity.RelatedDrugsEntity;

public class RelatedArticlesPaser extends SoapDataPaser {

	public RelatedDrugsEntity relatedDrugs;
	public ArticleDetailsEntity articleDetails;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("GetArticleInfoResult");
		String date;
		PropertyInfo propertyInfo;
		String EndFlag="};";
		//String[] Temp=new String[30];
		
		//String regEx="(?<==)[^;]+(?=;)";
		//Pattern p = Pattern.compile(regEx);
		propertyInfo = new PropertyInfo();
		articleDetails = new ArticleDetailsEntity();
		relatedDrugs = new RelatedDrugsEntity();
		articleDetails.recordLength = 0;
		
		do{
			try{
				detail.getPropertyInfo(articleDetails.recordLength, propertyInfo);
				date = detail.getProperty(articleDetails.recordLength).toString();
				if(date.equals("anyType{}"))
					date = "";
		        if(propertyInfo.name.equals("ID"))
				{
		        	articleDetails.id = date;
				}
				else if(propertyInfo.name.equals("Title"))
				{
					articleDetails.title=date;
				}
				else if(propertyInfo.name.equals("Authors"))
				{
					articleDetails.authors=date;
				}
				else if(propertyInfo.name.equals("JournalName"))
				{
					articleDetails.journalName=date;
				}
				else if(propertyInfo.name.equals("AuthorEntities"))
				{
					//articleDetails.AuthorEntities=date;
				}
				else if(propertyInfo.name.equals("DateAndVolume"))
				{
					//articleDetails.DateAndVolume=date;
				}
				else if(propertyInfo.name.equals("Abstract"))
				{
					articleDetails.abstracts=date;
				}
				else if(propertyInfo.name.equals("DrugsCount"))
				{
					articleDetails.drugsCount=date;
				}
				else if(propertyInfo.name.equals("RelatedDrugs"))
				{
					Integer ddd = new Integer(Integer.parseInt(articleDetails.drugsCount));
					int iLayer = ddd.intValue();
					relatedDrugs.recordLength = iLayer;

					if(iLayer > 0)
					{
						for(int j = 0; j < iLayer; j++)
						{
							Field[] fields = relatedDrugs.getClass().getDeclaredFields();  
							for (int i = 0; i < fields.length; i++) {            
								fields[i].setAccessible(true);             
								//detect String            
								if (fields[i].getType().equals(String[].class)) {                
									String tag = fields[i].getName() + "=";                
									if(date.contains(tag)){                    
										String strValue = date.substring(date.indexOf(tag)+tag.length(), date.indexOf(";", date.indexOf(tag)));                    
										if(strValue.length()!=0){
											//fields[i].set(Temp, strValue);
											if(fields[i].getName().equals("ID"))
												relatedDrugs.id[j] = strValue;
											else if(fields[i].getName().equals("Name_EN"))
												relatedDrugs.nameEn[j] = strValue;
											else if(fields[i].getName().equals("Name_CN"))
												relatedDrugs.nameCn[j] = strValue;
											else if(fields[i].getName().equals("ATC"))
												relatedDrugs.atc[j] = strValue;
										}
									}            
								}
							}
							date = date.substring(date.indexOf(EndFlag)+EndFlag.length());
						}
					}
				}
				else if(propertyInfo.name.equals("Source"))
				{
					articleDetails.source=date;
				}
				else if(propertyInfo.name.equals("KeyWords"))
				{
					articleDetails.keyWords=date;
				}
				
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			articleDetails.recordLength++;
		}while(date != null);
	
	}

}
