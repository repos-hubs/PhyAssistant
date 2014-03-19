package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.NewShareEntity;

public class SubmitResultParser extends SoapDataPaser{
	public String submitFlag="";

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
	   	PropertyInfo propertyInfo;
    	propertyInfo = new PropertyInfo();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("setSurveyResultNewResult");
		String strValue;
		NewShareEntity newShareEntity=new NewShareEntity();
		
		String[] resultKey = new String[] { "insertResult", "qtitle",
		"qcontent"};
		Log.e("insertResult", "insertResult");
		int j = 0;
		do{
			try{
    			detail.getPropertyInfo(j, propertyInfo);
    			date = detail.getProperty(j).toString();
    			
    			if(date.equals("anyType{}"))
					date = "";    			
    			else if(propertyInfo.name.equals("survyResultNew"))
    			{
//    				submitFlag = date;
    				
					int resultLength = resultKey.length;
					for (int i = 0; i < resultLength; i++) {
						String tag = resultKey[i] + "=";
						if (date.contains(tag)) {
							strValue = date.substring(
									date.indexOf(tag) + tag.length(),
									date.indexOf(";", date.indexOf(tag)));
							if (strValue.length() != 0&&!strValue.equals("anyType{}")) {
								if (resultKey[i].equals("insertResult"))
    			{
									submitFlag=strValue;
									newShareEntity.setInsertResult(strValue);
									
									Log.e("submitFlag", String.valueOf(submitFlag));
									Log.e("strValue1",strValue);
								}									
								else if (resultKey[i].equals("qtitle"))
								{
									newShareEntity.setQtitle(strValue);
									SharedPreferencesMgr.putSharingTitle(strValue);
									Log.e("strValue2",strValue);
								}
								else if (resultKey[i].equals("qcontent"))
								{
									newShareEntity.setQcontent(strValue);
									SharedPreferencesMgr.putSharingContent(strValue);
									Log.e("strValue3",strValue);
								}

							}
						}
					}
    				
    				
    			}

			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			j++;
		}while(date != null);
	}

}
