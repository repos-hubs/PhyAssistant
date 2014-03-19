package com.jibo.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.jibo.data.entity.ResearchBean;
import com.jibo.data.entity.RetrieveArticleEntity;

public class GetResearchSearch extends SoapDataPaser{
	public ArrayList<ResearchBean> list=new ArrayList<ResearchBean>();
	public int artCount=0;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
		String[] Temp=new String[30];
		
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("AdvancedRetrieveArticlesResult");
		PropertyInfo propertyInfo;
		//String EndFlag="};";
		String EndFlag="JournalName";
				
		propertyInfo = new PropertyInfo();

		int c = 0;
		int count=0;
		do{
			try{
				detail.getPropertyInfo(c, propertyInfo);
				date = detail.getProperty(c).toString();
				if(date.equals("anyType{}"))
					date = "";
								
//				if(propertyInfo.name.equals("TotalCount"))
//				{
//					advancedretrievearticle.TotalCount = date;
//				}
//				else 
				if(propertyInfo.name.equals("ArticleCount"))
				{
					artCount=Integer.parseInt(date);
				}
				else 
					if(propertyInfo.name.equals("ArticleList"))
				{
//					Integer ddd = new Integer(Integer.parseInt(advancedretrievearticle.ArticleCount));
//					int iLayer = ddd.intValue();

					if(artCount > 0)
					{
						for(int j = 0; j < artCount; j++)
						{
							ResearchBean advancedretrievearticle = new ResearchBean();
							Field[] fields = advancedretrievearticle.getClass().getDeclaredFields();  
							for (int i = 0; i < fields.length; i++) {            
								fields[i].setAccessible(true);             
								//detect String            
//								if (fields[i].getType().equals(String[].class)) {                
									String tag = fields[i].getName() + "=";                
									if(date.contains(tag)){                    
										String strValue = date.substring(date.indexOf(tag)+tag.length(), date.indexOf(";", date.indexOf(tag)));                    
										if(strValue.length()!=0){
											//fields[i].set(Temp, strValue);
											if(strValue.equals("anyType{}"))
												strValue = "";
											if(fields[i].getName().equals("ID"))
											{
												advancedretrievearticle.setID(strValue);
												Log.e("advancedretrievearticle.ID[j]",strValue);
												count++;
												Log.e("count12121212",String.valueOf(count)+"");

											}

											else if(fields[i].getName().equals("Title"))
												advancedretrievearticle.setTitle(strValue);
											else if(fields[i].getName().equals("Authors"))
												advancedretrievearticle.setAuthors(strValue);
											else if(fields[i].getName().equals("JournalName"))
												advancedretrievearticle.setJournalName(strValue);
											

//										}
									}
								}
							}
							list.add(advancedretrievearticle);
							date = date.substring(date.indexOf(EndFlag)+EndFlag.length());
						}
					}
				}
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
	
			c++;
			
		}while(date != null);
		
	}

}
