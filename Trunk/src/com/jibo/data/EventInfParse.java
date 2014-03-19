package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.RelatedBeans;

public class EventInfParse extends SoapDataPaser{
	public ArrayList<RelatedBeans> list=new ArrayList<RelatedBeans>();
	public RelatedBeans relatedBean;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
		PropertyInfo propertyInfo;
		//String[] Temp=new String[30];
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("GetEventInfoResult");
		int j=0;
		
		//String regEx="(?<==)[^;]+(?=;)";
		//Pattern p = Pattern.compile(regEx);
		propertyInfo = new PropertyInfo();
		relatedBean=new RelatedBeans();
		do{
			try{
				detail.getPropertyInfo(j, propertyInfo);
				date = detail.getProperty(j).toString();
				if(date.equals("anyType{}"))
					date = "";

				//Matcher m = p.matcher(date);
				
				//int test=0;
				//while(m.find()){
				//	Temp[test] = new String(m.group());
				//	test++;
				//}
				if(propertyInfo.name.equals("ID"))
				{
					relatedBean.setID(date) ;
				}
				else if(propertyInfo.name.equals("Name"))
				{
					relatedBean.setName(date);
				}
				else if(propertyInfo.name.equals("Place"))
				{
					relatedBean.setPlace(date);
				}
				else if(propertyInfo.name.equals("EventDate"))
				{
					relatedBean.setEventDate(date);
				}
				else if(propertyInfo.name.equals("Introduction"))
				{
					relatedBean.setIntroduction(date);
				}
				else if(propertyInfo.name.equals("Organizer"))
				{
					relatedBean.setOrganizer(date);
				}
				else if(propertyInfo.name.equals("Tel"))
				{
					relatedBean.setTel(date);
				}
				else if(propertyInfo.name.equals("Fax"))
				{
					relatedBean.setFax(date);
				}
				else if(propertyInfo.name.equals("Email"))
				{
					relatedBean.setEmail(date);

				}
				else if(propertyInfo.name.equals("Website"))
				{
					relatedBean.setWebsite(date);
				}
				/*switch(geteventinfo.iRecordLength){				
				case 0:
					geteventinfo.ID = date;
				    break;
				case 1:
					geteventinfo.name=date;
				    break;
				case 2:
					geteventinfo.place=date;
				    break;
				case 3:
					geteventinfo.eventDate=date;
				    break;
				case 4:
					geteventinfo.introduction=date;
				    break;
				case 5:
					geteventinfo.organizer=date;
				    break;
				case 6:
					geteventinfo.tel=date;
				    break;
				case 7:
					geteventinfo.fax=date;
				    break;
				case 8:
					geteventinfo.email=date;
					break;  
				case 9:
					geteventinfo.website=date;
					break;
				}*/
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			j++;
		}while(date != null);	
	}

}
