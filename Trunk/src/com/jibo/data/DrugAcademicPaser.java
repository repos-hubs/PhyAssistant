package com.jibo.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.AcademicEntity;


public class DrugAcademicPaser extends SoapDataPaser {
	public AcademicEntity academicActivities;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("GetAcademicActivities_newResult");
		String date;
		String[] Temp=new String[30];
		
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		academicActivities = new AcademicEntity();
		academicActivities.iRecordLength = 0;
		do{
			try{
				date = detail.getProperty(academicActivities.iRecordLength).toString();

				Matcher m = p.matcher(date);
				
				int test=0;
				while(m.find()){
					Temp[test] = new String(m.group());
					if(Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
				}
				academicActivities.id.add( Temp[0]);
				academicActivities.productID.add( Temp[1]);
				academicActivities.title.add( Temp[2]);
				academicActivities.authors.add( Temp[3]);
				academicActivities.journalName.add( Temp[4]);
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			academicActivities.iRecordLength++;
		}while(date != null);

	}

}
