package com.jibo.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.DrugEDLEntity;

/**基本药物目录解析*/
public class DrugEDLPaser extends SoapDataPaser {
	public DrugEDLEntity entity;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("GetDrugEDLsByRegionResult");
		String date;
		String[] Temp=new String[30];
		
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		entity = new DrugEDLEntity();
		entity.iRecordLength = 0;
		do{
			try{
				date = detail.getProperty(entity.iRecordLength ).toString();
				Matcher m = p.matcher(date);				
				int test=0;
				while(m.find()){
					Temp[test] = new String(m.group());
					if(Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
				}
				entity.productID.add(Temp[0]);
				entity.national.add(Temp[1]);
				entity.formulation.add(Temp[2]);
				entity.regional.add(Temp[3]);
				entity.categoryOfUse.add(Temp[4]);
				entity.source.add(Temp[5]);
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			entity.iRecordLength++;
		}while(date != null);
	}

}
