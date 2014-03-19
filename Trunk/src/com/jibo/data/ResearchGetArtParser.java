package com.jibo.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.RetrieveArticleEntity;

public class ResearchGetArtParser extends SoapDataPaser{
	public ArrayList<RetrieveArticleEntity> list=new ArrayList<RetrieveArticleEntity>();
	public RetrieveArticleEntity relatedBean;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		// TODO Auto-generated method stub
		String date;
		String[] Temp=new String[30];
		
		String regEx="(?<==)[^;]+(?=;)";
		Pattern p = Pattern.compile(regEx);
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result
				.getProperty("getTaArticleResult");
		int j = 0;
		do{
			try{
				date = detail.getProperty(j).toString();
				relatedBean=new RetrieveArticleEntity();
				Matcher m = p.matcher(date);
				
				int test=0;
				while(m.find()){
					Temp[test] = new String(m.group());
					if(Temp[test].equals("anyType{}"))
						Temp[test] = "";
					test++;
				}
				relatedBean.setId(Temp[0]);
				relatedBean.setTitle(Temp[1]);
				relatedBean.setAuthors(Temp[2]);
				relatedBean.setJournalName(Temp[3]);
				list.add(relatedBean);
			}catch (Exception e){
				e.printStackTrace();
				break;
			}
			j++;
		}while(date != null);
	}

}
