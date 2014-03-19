package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Pattern;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.text.TextUtils;
import android.util.Log;

import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.MessageBean;

public class NewGetMsgPaser extends SoapDataPaser{
	public ArrayList<MessageBean> al=new ArrayList<MessageBean>();

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
	   	PropertyInfo propertyInfo;
    	propertyInfo = new PropertyInfo();
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("getMessageShareResult");
		int j = 0;
		do{
			try{	
				MessageBean msgBean=new MessageBean();
				if(j>=detail.getPropertyCount())break;
    			detail.getPropertyInfo(j, propertyInfo);
    			date = detail.getProperty(j).toString();
    			if(date.equals("anyType{}"))
					date = "";
    			
    			else if(propertyInfo.name.equals("Deadline"))
    			{
    				msgBean.setDeadline(date);
    				Log.e("Deadline", date);
    				if(!date.equals(""))
    				{
        				SharedPreferencesMgr.putDeadLine(date);
    				}


    			}
    			else if(propertyInfo.name.equals("Msgcontent"))
    			{
    				msgBean.setMsgContent(date);
    				Log.e("Msgcontent",date);
                    if(!date.equals(""))
                    {
          				SharedPreferencesMgr.putMsgContent(date);
                    }

    			}
    			al.add(msgBean);

			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			j++;
		}while(date != null);
	}

}
