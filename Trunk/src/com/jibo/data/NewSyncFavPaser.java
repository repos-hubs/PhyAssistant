package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;
import com.jibo.data.entity.NewFavItemEntity;

import android.util.Log;

public class NewSyncFavPaser extends SoapDataPaser{
	public ArrayList<NewFavItemEntity> al=new ArrayList<NewFavItemEntity>();

	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
		String[] Temp=new String[30];
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("GetCustomer_FavoriteResult");
		
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);

		int j = 0;
		do{
			try{
				date = detail.getProperty(j).toString();

				Matcher m = p.matcher(date);
				
				int test=0;
				while(m.find()){
					Temp[test] = new String(m.group());
					if(Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
				}
				NewFavItemEntity newFav = new NewFavItemEntity();
				newFav.setFavId(Temp[1]);
//				Log.e("Temp[1]",Temp[1]);
				newFav.setFavName(Temp[2]);
//				Log.e("Temp[2]",Temp[2]);
				newFav.setCategoryId(Temp[3]);
//				Log.e("Temp[3]",Temp[3]);
				al.add(newFav);

			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			j++;
		}while(date != null);
	}

}
