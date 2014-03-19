package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.RetrieveArticleEntity;

public class RetrieveCoauthorPaser extends SoapDataPaser {
	public ArrayList<RetrieveArticleEntity> list;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		list=new ArrayList<RetrieveArticleEntity>();
		SoapObject obj = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) obj.getProperty("RetrieveCoauthorArticlesResult");
		System.out.println("count    "+detail.getPropertyCount());
		for(int i=0; i<detail.getPropertyCount(); i++) {
			SoapObject item = (SoapObject) detail.getProperty(i);
			RetrieveArticleEntity en = new RetrieveArticleEntity();
			en.setId(item.getProperty("ID").toString());
			en.setTitle(item.getProperty("Title").toString());
			en.setAuthors(item.getProperty("Authors").toString());
			en.setJournalName(item.getProperty("JournalName").toString());
			list.add(en);
		}
	}
	public ArrayList<RetrieveArticleEntity> getList() {
		return list;
	}
	public void setList(ArrayList<RetrieveArticleEntity> list) {
		this.list = list;
	}
}
