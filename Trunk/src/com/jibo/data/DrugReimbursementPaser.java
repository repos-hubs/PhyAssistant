package com.jibo.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.DrugReimbursementsEntity;


public class DrugReimbursementPaser extends SoapDataPaser{

	public DrugReimbursementsEntity drugreimbursements;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("GetDrugReimbursementsByRegionResult");
		String date;
		String[] Temp=new String[9];
		
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		drugreimbursements = new DrugReimbursementsEntity();
		drugreimbursements.iRecordLength = 0;
		do{
			try{
				date = detail.getProperty(drugreimbursements.iRecordLength).toString();

				Matcher m = p.matcher(date);
				
				int test=0;
				while(m.find()){
					Temp[test] = new String(m.group());
					if(Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
					if(test==9)
						break;
				}
				drugreimbursements.productID.add(Temp[0]);
				drugreimbursements.national.add(Temp[1]);
				drugreimbursements.formulation.add(Temp[2]);
				drugreimbursements.reimbursementRate.add(Temp[3]);
				drugreimbursements.type.add(Temp[4]);
				drugreimbursements.categoryOfUse.add(Temp[5]);
				drugreimbursements.pediatricSpecific.add(Temp[6]);
				drugreimbursements.regional.add(Temp[7]);
				drugreimbursements.source.add(Temp[8]);
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			drugreimbursements.iRecordLength++;
		}while(date != null);
	}

}
